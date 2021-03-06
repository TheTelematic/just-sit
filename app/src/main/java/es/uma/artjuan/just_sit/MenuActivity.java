package es.uma.artjuan.just_sit;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.client.Firebase;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.port;

public class MenuActivity extends AppCompatActivity {
    private static int mesa=0;
    private EditText nmesa;
    //private Activity activity = getActivity();
    private Context context=this;
    private Context contentTxt;
    private ServerInfo server;
    private static String FIREBASE_URL = "https://barnotificaciones.firebaseio.com/";
    private MyCustomAdapter dataAdapter = null;
    private ArrayList<Plato> platoList = new ArrayList<>();
    private String[] valueList= new String[Menu.getTam()];
    //private Button floatingAddButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        nmesa=(EditText)findViewById(R.id.nmesa);

        if(mesa != 0){
            nmesa.setText(String.valueOf(mesa));
            nmesa.setFocusable(false);
        }

        server = ServerInfo.getInstance();
        //Generate list View from ArrayList
        displayListView();

        checkButtonClick();

    }


    private void displayListView() {

        platoList = Menu.getSingleton().getListaMenu();
        if(platoList==null){
            Toast.makeText(this,"_________________ERRROR________________",Toast.LENGTH_LONG).show();
        }

        for(int k = 0; k < platoList.size(); k++){
            Plato p = platoList.get(k);
            p.setMarcado(false);
            platoList.set(k, p);
        }


        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this, R.layout.plato_info, platoList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Plato plato = (Plato) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + plato.getNombre(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Plato> {

        private ArrayList<Plato> platoList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Plato> platoList) {
            super(context, textViewResourceId, platoList);
            this.platoList = new ArrayList<Plato>();
            this.platoList.addAll(platoList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
            EditText cant;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.plato_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.cant = (EditText) convertView.findViewById(R.id.cant);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Plato plato = (Plato) cb.getTag();
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        plato.setMarcado(cb.isChecked());
                    }
                });
                holder.cant.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        valueList[position] = s.toString();
                        Plato plato = (Plato) holder.name.getTag();
                        if(valueList[position].equals("")){
                            holder.name.setChecked(false);
                            plato.setMarcado(false);
                        }else{
                            holder.name.setChecked(true);
                            plato.setMarcado(true);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.i("AfterTextChange",holder.cant.getText().toString());
                    }
                });
            }
            else {
                holder = (MyCustomAdapter.ViewHolder) convertView.getTag();
            }

            Plato plato = platoList.get(position);
            holder.code.setText(" (" +  plato.getPrecio() + "€)");
            holder.name.setText(plato.getNombre());
            holder.name.setChecked(plato.isMarcado());
            holder.name.setTag(plato);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String et = nmesa.getText().toString();

                if(et.equals("")){
                    Toast.makeText(context, "Debe indicar una mesa", Toast.LENGTH_LONG).show();
                }else{
                    mesa = Integer.parseInt(et);

                    MyATaskMenu myATaskmenu = new MyATaskMenu();
                    myATaskmenu.execute(platoList);
                }



            }
        });

    }
/*
    public static void sendNotificationToUser(String user, final String message) {
        Firebase ref = new Firebase(FIREBASE_URL);
        final Firebase notifications = ref.child("notificationRequests");

        Map notification = new HashMap<>();
        notification.put("username", user);
        notification.put("message", message);

        notifications.push().setValue(notification);
    }
*/
    private class MyATaskMenu extends AsyncTask<ArrayList<Plato>,Void,String> {

        ProgressDialog progressDialog;
        String estadoPed="";
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("Conectando al servidor");
            progressDialog.setMessage("Espera por favor...");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(ArrayList<Plato>... values){

            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(server.getAddress(), server.getPort()), 4000);
                Mensajes m = new Mensajes();

                ArrayList<String> id_platos = new ArrayList<>();
                ArrayList<Integer> cantidad_platos = new ArrayList<>();

                int k = 0;
                for(Plato plato : values[0]){

                    if(plato.isMarcado()){
                        String id = plato.getId();

                        id_platos.add(id);
                        if(valueList[k]==null){
                            plato.setCantidad(0);
                            Toast.makeText(context, "Introduce cantidad de " + plato.getNombre(), Toast.LENGTH_SHORT).show();
                        }
                        plato.setCantidad(Integer.parseInt(valueList[k]));
                        cantidad_platos.add(plato.getCantidad());
                        }
                    k++;
                }


                System.out.print("Haciendo pedido...\n");
                estadoPed= m.hacerPedido(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                        id_platos,
                        mesa,cantidad_platos,
                        new BufferedReader(new InputStreamReader(socket.getInputStream())));
                System.out.print("Pedido hecho...\n");
                if(estadoPed.equals(Mensajes.Comandos.PEDIDO_OK)) {
                    Bar.getInstance().setOcupado(mesa, true);
                }
                socket.close();
                return estadoPed;
            }catch (UnknownHostException ex) {
                Log.e("E/TCP Client", "" + ex.getMessage());
                return ex.getMessage();
            } catch (SocketTimeoutException e){
                e.printStackTrace();
                System.out.print("TIMEOUT\n");

                return e.getMessage();
            } catch (IOException ex) {
                Log.e("E/TCP Client", "" + ex.getMessage());
                return ex.getMessage();
            }catch (NullPointerException e){
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String value){
            progressDialog.dismiss();//oculta ventana emergente

            if(estadoPed.equals("OK - " + Mensajes.Comandos.PEDIDO_OK)){
                Toast.makeText(context,"Oido cocina! Pedido realizado.",Toast.LENGTH_SHORT).show();
                //sendNotificationToUser("puf", "Hi there puf!");
                Intent intent = new Intent(context, PedidoActivity.class);
                startActivity(intent);
               // platoList.clear();
                //dataAdapter.notifyDataSetChanged();
                finish();
            }else{
                //Toast.makeText(context,"¿Que demonios ha pasado?",Toast.LENGTH_SHORT).show();
                Toast.makeText(context,"El servidor se esta tomando un cafe, vuelve a intentarlo mas tarde",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
