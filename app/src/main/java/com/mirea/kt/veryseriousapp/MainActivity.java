package com.mirea.kt.veryseriousapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private List<Contact> contactList;
    private ContactAdapter contactAdapter;
    private EditText nameField;
    private EditText phoneField;
    private EditText urlField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Myapppp", "onCreate: main created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        ListView phoneBookListView = findViewById(R.id.phone_book_list_view);

        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this,this, contactList);

        fetchContactData();
        phoneBookListView.setAdapter(contactAdapter);

        Button addContactButton = findViewById(R.id.add_contact_button);
        addContactButton.setOnClickListener(view -> {
            openEditContactScreen(null, getApplicationContext());
        });
    }

    private void fetchContactData() {   // Собираем данные из БД

        contactList.clear();
        Log.d("Myapppp", "fetchContactData: getting contact");
        List<Contact> contacts = dbHelper.getAllContacts();
        contactList.addAll(contacts);
        contactAdapter.notifyDataSetChanged();
    }

    public void openEditContactScreen(Contact contact, Context context) { // Окно редактирования + его функионал

        View bottomSheetView = getLayoutInflater().inflate(R.layout.edit_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        nameField = bottomSheetView.findViewById(R.id.name_edit_field);
        phoneField = bottomSheetView.findViewById(R.id.number_edit_field);
        urlField = bottomSheetView.findViewById(R.id.avatar_url_field);
        Button addButton = bottomSheetView.findViewById(R.id.add_edit_button);
        Button deleteButton = bottomSheetView.findViewById(R.id.delete_button);
//        Button addFileButton = bottomSheetView.findViewById(R.id.add_file_button); TODO УВЫ пока не работает
        Button shareButton = bottomSheetView.findViewById(R.id.share_button);

        if (contact != null){                       // Заполняем данные в полях ввода если есть
            nameField.setText(contact.getName());
            phoneField.setText(contact.getPhone());
            urlField.setText(contact.getAvatar());
        }
        else {
            deleteButton.setVisibility(View.INVISIBLE);
            shareButton.setVisibility(View.VISIBLE);
        }

        addButton.setOnClickListener(v -> {                 //Добавляем в БД
            String name = nameField.getText().toString();
            String phone = phoneField.getText().toString();
            String avatar= urlField.getText().toString();

            Contact contact1 = new Contact(name, phone, avatar);
            if (contact != null){
                dbHelper.deleteContact(contact);
            }
            dbHelper.insertContact(contact1);
            fetchContactData();
            bottomSheetDialog.dismiss();
        });

        deleteButton.setOnClickListener(v -> {               // Удаляем контакт
            dbHelper.deleteContact(contact);
            fetchContactData();
            bottomSheetDialog.dismiss();
        });

        /*addFileButton.setOnClickListener(v -> {           TODO УВЫ пока не работает
            // Open the file picker
            Intent getFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getFileIntent.setType("image/*");  // Set the MIME type to filter specific file types if needed
            startActivity(getFileIntent);
            startActivityForResult(getFileIntent, REQUEST_CODE);
            urlField.setText("aaa");

        });*/

        shareButton.setOnClickListener(v -> {                    //Делимся контактом
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String textToShare = "Имя: " + contact.getName() + "\n" + "Телефон: " + contact.getPhone();
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);

            if (context != null) {
                context.startActivity(Intent.createChooser(shareIntent, "Поделиться контактом"));
            }
        });

    }
}