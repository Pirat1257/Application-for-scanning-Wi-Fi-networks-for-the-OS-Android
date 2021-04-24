package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class Info extends AppCompatActivity {

    private Button close_button;
    private ListView info_list;
    private ArrayList<String> array_list;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        // Привязываем кнопку и действие при ее нажатии
        close_button = findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() { // Добавляем действие при нажатии на кнопку
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Настраиваем адаптер для вывода информации
        array_list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array_list);
        // Привязываем адапер к info_list
        info_list = findViewById(R.id.info);
        info_list.setAdapter(adapter);
        // Получаем информацию от MainActivity
        Bundle arguments = getIntent().getExtras();
        // Добавляем информацию
        array_list.add("SSID: " + arguments.get("SSID").toString());
        array_list.add("BSSID: " + arguments.get("BSSID").toString());
        array_list.add("Level: " + calculateSignalLevel(arguments.getInt("Level")));
        array_list.add("Frequency: " + arguments.getInt("Frequency") + " MHz");
        array_list.add("Capabilities: " + arguments.get("Capabilities").toString());
        adapter.notifyDataSetChanged(); // Обновляем список
    }

    private String calculateSignalLevel(int lvl)
    {
        if (lvl > -50) return "Excellent";
        else if (-60 >= lvl && lvl >= -50) return "Good";
        else if (-70 >= lvl && lvl >= -60) return "Fair";
        else if (-70 >= lvl) return "Weak";
        else return "No signal";
    }
}