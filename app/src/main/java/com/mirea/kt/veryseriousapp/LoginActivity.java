package com.mirea.kt.veryseriousapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText usernameField;
    private EditText passwordField;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Myapppp", "onCreate: Login created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.username_field);
        passwordField = findViewById(R.id.password_field);
        Button loginButton = findViewById(R.id.login_button);
        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> {       // Логин + секретный вход

            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();

            if ((username.equals("1")) && (password.equals( "1"))) {
                Log.d("Myapppp", "onClick: Secret entry");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();}

            else if (username.equals("2") && password.equals("2")){
                try {
                    Log.d("Myapppp", "onClick: login attempt start");
                    sendLoginRequest("Student30132", "fHr1uvB");

                } catch (Exception e) {
                    Log.e("Myapppp", e.toString());
                    throw new RuntimeException(e);
                }
            }

            else {
                try {
                    Log.d("Myapppp", "onClick: login attempt start");
                    sendLoginRequest(username, password);

                } catch (Exception e) {
                    Log.e("Myapppp", e.toString());
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void sendLoginRequest(String lgn, String pwd) {     // Страшно всё подсвечивает
        AsyncTask<String, Void, String> loginTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {     // Отправка данных на сервер
                String result = "";
                try {
                    URL url = new URL("https://android-for-students.ru/coursework/login.php");

                    String g = "RIBO-03-21";

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write("lgn=" + params[0] + "&pwd=" + params[1] + "&g=" + g);
                    writer.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    writer.close();
                    conn.disconnect();

                    result = response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    errorText = findViewById(R.id.error_text);
                    errorText.setVisibility(View.VISIBLE);
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {   // Проверка данных с сервера + заполнение БД + переход на главный экран

                Log.d("Myapppp", "sendLoginRequest: json to db start");
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.toString().contains("\"result_code\":1")) {
                        if(dbHelper.isDatabaseEmpty()) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject contactObject = data.getJSONObject(i);
                                String name = contactObject.getString("name");
                                String phone = contactObject.getString("phone");
                                String avatar = contactObject.optString("avatar", "");

                                Contact contact = new Contact(name, phone, avatar);
                                dbHelper.insertContact(contact);
                            }
                        }

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("Myapppp", "sendLoginRequest: login attempt failed");
                        errorText = findViewById(R.id.error_text);
                        errorText.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        loginTask.execute(lgn, pwd);
    }
}
