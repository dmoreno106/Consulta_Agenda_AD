package org.izv.dmc.consultaagendaad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.UserDictionary;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final String TAG="xyzyx";
    private final int CONTACT_PERMISSION=1;

    private Button btsearch;// = findViewById(R.id.btSearch); no se puede hacer: el layout todavia no existe por lo que no puede referenciarse un elemento del layout
    private EditText etPhone;
    private TextView tvResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG,"onCreate");//verbose
        initialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG,"onRequestPermissions");
        switch (requestCode){
            case CONTACT_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //permiso
                    search();
                }else{
                    //sin permiso
                }
                break;
        }
        //request code
        //permissions
        //grantResults
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG,"onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG,"onStop");
    }


    private void explain() {
        showRationaleDialog(getString(R.string.title),getString(R.string.message),Manifest.permission.READ_CONTACTS,CONTACT_PERMISSION);

    }

    private void initialize() {
        btsearch = findViewById(R.id.btSearch);
        etPhone = findViewById(R.id.etPhone);
        tvResult = findViewById(R.id.tvResult);
        btsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIfPermitted();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},CONTACT_PERMISSION);
    }


    private void search() {

        //tvResult.setText("a pelo ya si");
        //ContentProvider Proveedor de contenidos
        //ContentResolver Consultor de contenidos
        //Queries the user dictionary and returns results
        //url:https:ieszaidinvergeles.org/carpeta/carpeta2/pagina.html?dato=1
        //uri:protocolo://direccion/rutar/recurso
/*
        Cursor cursor=getContentResolver().query(UserDictionary.Words.CONTENT_URI,  //The content URI of the words table
                new String[]{"projection"},                                         //The columns to return for each row
                "campo1=? and campo2>? or campo3=? ",                                         //Selection criteria
                new String[]{"pepe","4","23"},                                     //The sort order for the returned rows
                "campo5,campo3,campo4");

 */
        /*
        Uri uri= ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[]=new String[]{ContactsContract.Contacts.DISPLAY_NAME};
        String seleccion=ContactsContract.Contacts.IN_VISIBLE_GROUP+"=? and"+ContactsContract.Contacts.HAS_PHONE_NUMBER+"=?";
        String argumentos[]= new String[]{"1,1"};
        seleccion=null;
        argumentos=null;
        String orden=ContactsContract.Contacts.DISPLAY_NAME+"collate localized asc";
        Cursor cursor=getContentResolver().query(uri,proyeccion,seleccion,argumentos,orden);
        String[] columnas=cursor.getColumnNames();
        for (String s:columnas) {
           Log.v(TAG,s);

        }
        String displayName;
        int columna=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        while (cursor.moveToNext()){
            displayName=cursor.getString(columna);
            Log.v(TAG,displayName);

        }
*/
        String number2="0";
        try {
             number2 = String.valueOf(etPhone.getText().charAt(0) + "%");
        }catch (Exception e){

        }

        String[] projeccion2 = new String[] { ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE };
        String selection2=ContactsContract.Data.MIMETYPE+"=?"+" AND "+ContactsContract.CommonDataKinds.Phone.NUMBER +"= ?";
        String[] arguments2 =new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,number2};
        String order2 = ContactsContract.Data.DISPLAY_NAME + " ASC";

        Cursor cursor2 = getContentResolver().query(ContactsContract.Data.CONTENT_URI, projeccion2, selection2, arguments2, order2);

        tvResult.setText("");
        while(cursor2.moveToNext()){
            tvResult.append("Nombre: " + cursor2.getString(0) + " Número: " + cursor2.getString(1)+"\n");
        }
        cursor2.close();

           /*
        int columnaNombre=cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int columnaNumero=cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String nombre,numero;
        while (cursor2.moveToNext()){
            nombre=cursor2.getString(columnaNombre);
            numero=cursor2.getString(columnaNumero);
            Log.v(TAG,nombre+":"+numero);
        }

        for (String s:columnas2) {
            int pos= cursor2.getColumnIndex(s);
            String valor=cursor2.getString(pos);
            Log.v(TAG,pos+valor);
        }*/
    }

    private void searchIfPermitted() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //la versión de android es posterior a la 6 o incluida
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

                //ya se ha obtenido el permiso
                search();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explain();//2ªejecución
            } else {
                requestPermissions();//1ªejecución
            }
        } else {
            //la versión de android es anterior a la 6
            //ya se ha obtenido el permiso
            search();

        }
    }
        private void showRationaleDialog(String title,String message,String permission,int requestCode) {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);

            builder.setTitle(title)
                    .setMessage(message)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //nada
                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions();
                        }
                    });

            builder.create().show();


    }
}