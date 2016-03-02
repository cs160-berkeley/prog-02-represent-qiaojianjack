package cs160.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EntryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        Typeface sansSerifLight = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Light.ttf");

        final EditText zipcode_input = (EditText) findViewById(R.id.zipcode_input);
        zipcode_input.setTypeface(sansSerifLight);
        zipcode_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (!event.isShiftPressed()) {
                        // the user is done typing.
                        // process zipcode here
                        //TODO: location might not be coded by zipcode, change!
                        Intent toRepListView = new Intent(EntryActivity.this, RepList.class);
                        String zipcode = zipcode_input.getText().toString(); // consume.
                        toRepListView.putExtra("zipcode", zipcode);
                        EntryActivity.this.startActivity(toRepListView);

                        return true;
                    }
                }
                return false;
            }
        });


        Button locationButton = (Button) findViewById(R.id.location_button);
        locationButton.setTypeface(sansSerifLight);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect location here
                String zipcode = getLocation();
                Intent toRepListView = new Intent(EntryActivity.this, RepList.class);
                toRepListView.putExtra("zipcode", zipcode);
                EntryActivity.this.startActivity(toRepListView);

            }
        });

    }

    private String getLocation() {
        return "94720";
    }

    public static String randomizeLocation() {
        return "94709";
    }
}
