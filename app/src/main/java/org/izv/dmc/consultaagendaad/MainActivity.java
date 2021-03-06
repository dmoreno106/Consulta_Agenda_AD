package org.izv.dmc.consultaagendaad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.izv.dmc.consultaagendaad.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    private static final String FIRSTTIME="firstTime";
    private final String TAG="xyzyx";
    private final int CONTACT_PERMISSION=1;

    private Button btsearch;
    private EditText etPhone;
    private TextView tvResult;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences= android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        Log.v(TAG,"onCreate");//verbose
        initialize();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ajustes) {
            ViewSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        SharedPreferences preferenciasActividad=getPreferences(Context.MODE_PRIVATE);
        String lastSearch=preferenciasActividad.getString(getString(R.string.last_Search),"");
        if(!lastSearch.isEmpty()){
            etPhone.setText(lastSearch);
        }

        //este if sirve para que la primera vez que se instale la app y no tenga permisos
        //no intente hacer la b??squeda de contactos (con lo cual pedir?? que le demos permisos nada m??s abrirla), as?? cuando nos pida permisos ser?? cuando
        //realizaremos la b??squeda. En la siguiente ejecuci??n los permisos
        if(getFirstTime()){
            searchIfPermitted();

        }

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

    public void  setFirstTime(){
        SharedPreferences.Editor editor =preferences.edit();
        editor.putBoolean(FIRSTTIME,true);
        editor.commit();
    }

    public boolean getFirstTime(){
        return preferences.getBoolean(FIRSTTIME,false);
    }


    private void search() {
        setFirstTime();
        tvResult.setText("");
        String phone=etPhone.getText().toString();
        phone=searchFormat(phone);
        SharedPreferences sharedPreferences =PreferenceManager.getDefaultSharedPreferences(this);

        String cleanSearch=phone.replace("%","");
        SharedPreferences prefenciasActividad=getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefenciasActividad.edit();
        editor.putString(getString(R.string.last_Search),cleanSearch);
        editor.commit();

        //<--shared preferences (apuntes)-->
       /* SharedPreferences p1=getSharedPreferences("preferenciascompartidas",Context.MODE_PRIVATE);
        SharedPreferences p2=getPreferences(Context.MODE_PRIVATE);
        SharedPreferences p4=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor ed1=p1.edit();
        SharedPreferences.Editor ed2=p2.edit();
        SharedPreferences.Editor ed4= p4.edit();

        ed1.putString("ved1","v1");//preferenciascompartidas.xml
        ed2.putString("ved2","v2");//MainActivity.xml
        ed4.putString("ved4","v4");//org.izv.dmc.consultaagendaad_preferences
        */
        SharedPreferences p3=PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed3=p3.edit();
        ed3.putString("ved3","v3");//org.izv.dmc.consultaagendaad_preferences

        String email = sharedPreferences.getString("email", getString(R.string.no_email));
        String username = sharedPreferences.getString("username", getString(R.string.no_username));
        tvResult.append(email+"\n"+username+"\n\n");

      searchContacts(phone);
    }

    private void searchContacts(String phone) {
        Uri uri2=ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Log.v(TAG,uri2.toString());

        String[] projeccion2 = new String[] { ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE };
        String selection2=ContactsContract.Data.MIMETYPE+"=?"+" AND "+ContactsContract.CommonDataKinds.Phone.NUMBER +" like ?";
        String[] arguments2 =new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,phone};
        String order2 = ContactsContract.Data.DISPLAY_NAME + " ASC";

        Cursor cursor2 = getContentResolver().query(ContactsContract.Data.CONTENT_URI, projeccion2, selection2, arguments2, order2);

        tvResult.append("Coincidencias: \n");
        while(cursor2.moveToNext()){
            tvResult.append("-Nombre: " + cursor2.getString(0) + " N??mero: " + cursor2.getString(1)+"\n");
        }
        cursor2.close();
    }

    private String searchFormat(String phone) {
        String newString="";
        for (char ch: phone.toCharArray()){
            newString+=ch+"%";
        }
        return newString;
    }

    private void searchIfPermitted() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //la versi??n de android es posterior a la 6 o incluida
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

                //ya se ha obtenido el permiso
                search();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explain();//2??ejecuci??n
            } else {
                requestPermissions();//1??ejecuci??n
            }
        } else {
            //la versi??n de android es anterior a la 6
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




    private void ViewSettings() {
        //intent -> intenci??n
        //intenciones explicitas o implicitas
        //explicita:definir que quiero ir desde el contexto actual a un contexto que se crea con la clase SettingsActivity

        Intent intent =new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}

//ContentProvider Proveedor de contenidos
//ContentResolver Consultor de contenidos
//Queries the user dictionary and returns results
//url:https:ieszaidinvergeles.org/carpeta/carpeta2/pagina.html?dato=1
//uri:protocolo://direccion/rutar/recurso