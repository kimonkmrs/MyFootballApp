package com.example.foorballapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView back, menu;
    private boolean isAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Retrieve the role information from the Intent
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        // Handle UI and functionality based on user role
        if (isAdmin) {
            // Show admin-specific options
            Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
            // You can now enable admin-specific features here
        } else {
            // Show regular user options
            Toast.makeText(this, "Welcome User!", Toast.LENGTH_SHORT).show();
            // You can now restrict access or hide admin features here
        }

        back = findViewById(R.id.left_icon);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous activity or layout
                onBackPressed();
            }
        });

        menu = findViewById(R.id.right_icon);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menus, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_item_layout1) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);

                }
                return true;
            }
        });

        popupMenu.show();


    }
}