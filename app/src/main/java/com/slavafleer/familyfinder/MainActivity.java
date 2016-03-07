package com.slavafleer.familyfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Regions;
import com.slavafleer.familyfinder.constant.AWSConstants;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                AWSConstants.IDENTITY_POOL_ID, // Identity Pool ID
                Regions.EU_WEST_1 // Region
        );
    }

    public void buttonEdit_onClick(View view) {

        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        EditText editTextFamilyRole = (EditText) findViewById(R.id.editTextFamilyRole);

        final String name = editTextName.getText().toString().trim();
        String familyRole = editTextFamilyRole.getText().toString().trim();

        if(name.isEmpty() || familyRole.isEmpty()) {

            Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize the Cognito Sync client
        CognitoSyncManager syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.EU_WEST_1, // Region
                credentialsProvider);

        // Create a record in a dataset and synchronize with the server
        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
        dataset.put(AWSConstants.KEY_NAME, name);
        dataset.put(AWSConstants.KEY_FAMILY_ROLE, familyRole);
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, List newRecords) {
                //Your handler code here
                Log.i(TAG, "User: " + name + "was updated");
            }
        });
    }
}
