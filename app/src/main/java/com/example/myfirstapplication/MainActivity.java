package com.example.myfirstapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;
import android.net.wifi.ScanResult;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button reload_button; // Кнопка обновления списка Wifi
    private ListView wifi_list; // Список Wifi
    private WifiManager wifi_manager; // Для работы с Wifi
    private ArrayList<String> array_list; // Для вывода названий
    private ArrayAdapter adapter; // Для строк
    private List<ScanResult> scan_results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Вывод экрана activity_main
        // Получение доступа к необходимым элементам
        reload_button = findViewById(R.id.reload_button); // Получаем доступ к кнопке через id
        reload_button.setOnClickListener(new View.OnClickListener() { // Добавляем действие при нажатии на кнопку
            @Override
            public void onClick(View v) {
                reload();
            }
        });
        wifi_list = findViewById(R.id.WifiSpots);
        wifi_manager =  (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        array_list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array_list);
        // Проверка на активность wifi
        wifi_manager.getWifiState();
        if (wifi_manager.getWifiState() == 0) {
            wifi_manager.setWifiEnabled(true);
        }
        wifi_list.setAdapter(adapter);
        // Прослушивание событий элемента ListView, а именно пользователь нажимает на один из пунктов списка
        wifi_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, Info.class);
                // Передаем информацию, которую надо будет вывести
                //intent.putExtra("SSID", position);
                int count = 0; // Для подсчета номера строки
                for(ScanResult scanResult : scan_results)
                {
                    if (count == position)
                    {
                        intent.putExtra("SSID", scanResult.SSID); // The network name.
                        intent.putExtra("BSSID", scanResult.BSSID); // The address of the access point.
                        intent.putExtra("Level", scanResult.level); // The detected signal level in dBm, also known as the RSSI.
                        intent.putExtra("Frequency", scanResult.frequency); // The primary 20 MHz frequency (in MHz) of the channel over which the client is communicating with the access point.
                        intent.putExtra("Capabilities", scanResult.capabilities); // Describes the authentication, key management, and encryption schemes supported by the access point.
                        break;
                    }
                    else
                        count++;
                }
                // Выводим окно
                startActivity(intent);

            }
        });
        reload();
    }

    // Приемник широковещательных сообщений для информации о WiFi точках
    private BroadcastReceiver wifi_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(this);
            scan_results = wifi_manager.getScanResults();
            for (ScanResult scanResult : scan_results) {
                //array_list.add(scanResult.SSID + "-" + scanResult.capabilities + "-" + "|" + scanResult.level+ "|" +"-" + scanResult.BSSID);
                array_list.add(scanResult.SSID);
                adapter.notifyDataSetChanged();
            }
        }
    };

    // Обновляем таблицу и сканируем доступные точки
    private void reload() {
        registerReceiver(wifi_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        array_list.clear(); // Убираем сохраненные названия
        Toast.makeText(this,"Scaning Wifi ...",Toast.LENGTH_SHORT).show();
        wifi_manager.startScan();
    }
}