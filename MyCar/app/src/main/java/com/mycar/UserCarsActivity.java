package com.mycar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;

public class UserCarsActivity extends AppCompatActivity {

    private static String baseURL = "http://192.168.1.101/MyCar/";

    private String brand;
    private String plate;
    private String id;
    private int car_id;
    private Object CarAdapter;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cars);

        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_gray)));
        getSupportActionBar().setTitle("Your Cars");

        listView = findViewById(R.id.listView);

        Intent intent = getIntent();
        id = intent.getStringExtra("user_id");

        getUserCars();
    }
    
    private void getUserCars(){
        String URL = String.format(baseURL + "getUserCars.php?id=%1$s", id);

        ArrayList<Car> arrayList = new ArrayList<>();
        ArrayList<Integer> carIDs = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, response -> {

            try{
                JSONArray array = new JSONArray(response);
                for(int i = 0; i < array.length(); i++){
                    JSONObject object = array.getJSONObject(i);

                    brand = object.getString("brand");
                    plate = object.getString("plate") ;
                    car_id = object.getInt("id");

                    arrayList.add(new Car(brand, plate));
                    carIDs.add(car_id);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            CarAdapter = new CarAdapter(UserCarsActivity.this, R.layout.custom_list_view, arrayList);
            listView.setAdapter((ListAdapter) CarAdapter);

            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent1 = new Intent(getApplicationContext(), CarInfoActivity.class);
                intent1.putExtra("carBrand", arrayList.get(i).getBrand());
                intent1.putExtra("user_id", id);
                intent1.putExtra("car_id", Integer.toString(Integer.parseInt(carIDs.get(i).toString())));
                startActivity(intent1);
            });
        }, error -> Toast.makeText(UserCarsActivity.this, error.toString(), Toast.LENGTH_SHORT).show());

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void goToAddCar(View v){
        Intent intent = new Intent(UserCarsActivity.this, AddCarActivity.class);
        intent.putExtra("user_id", id);
        startActivity(intent);
    }
}