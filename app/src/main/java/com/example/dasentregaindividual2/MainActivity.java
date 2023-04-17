package com.example.dasentregaindividual2;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.dasentregaindividual2.lista_partidos.ListaPartidosFragment;
import com.example.dasentregaindividual2.menu_principal.MenuPrincipalFragment;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
    implements MenuPrincipalFragment.ListenerMenuPrincipalFragment,
               ListaPartidosFragment.ListenerListaPartidosFragment {

    private AppBarConfiguration appBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Se reciben los datos que acompañan a la notificación FCM */
        if (getIntent().getExtras() != null) {
            String mensaje = getIntent().getExtras().getString("mensaje");
            String fecha = getIntent().getExtras().getString("fecha");

            Log.d("MainActivity", "mensaje --> " + mensaje);
            Log.d("MainActivity", "fecha --> " + fecha);
        }

        solicitarPermisosNotificaciones();

        NotificationManager elManager = (NotificationManager)
            getSystemService(Context.NOTIFICATION_SERVICE);
        elManager.cancel(1);

        /* Para que el nombre del 'app bar' cambie en función del fragmento en el que estamos */
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupActionBarWithNavController(this, navController,
                appBarConfiguration);
        }

        inicializarPreferencias();
    }

    /* Función para que la flecha de la 'app bar' nos lleve al fragmento anterior */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation
            .findNavController(this, R.id.nav_host_fragment);
        return NavigationUI
            .navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private void solicitarPermisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
            }
        }
    }

    private void inicializarPreferencias() {
        SharedPreferences preferencias = PreferenceManager
            .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferencias.edit();
        String idiomaSeleccionado = preferencias.getString("idioma", null);
        if (idiomaSeleccionado == null) { // Por defecto poner castellano como idioma
            editor.putString("idioma", "Castellano");
            LocaleListCompat appLocale = LocaleListCompat.forLanguageTags("es");
            AppCompatDelegate.setApplicationLocales(appLocale);
        }
        boolean modoOscuro = preferencias.getBoolean("modo_oscuro", false);
        if (modoOscuro)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        editor.apply();
    }

    @Override
    public void crearNotificacionesPartido(ArrayList<String> listaEquiposFavoritos) {
        Iterator<String> itr = listaEquiposFavoritos.iterator();
        int idNotificacion = 1;
        while (itr.hasNext()) {
            String equipoFavoritoActual = itr.next();
            crearNotificacionPartido(idNotificacion, equipoFavoritoActual, listaEquiposFavoritos.size());
            idNotificacion++;
        }
    }

    private void crearNotificacionPartido(int idNotificacion, String equipoFavorito, int cantidadEquiposFavoritos) {
        NotificationManager managerNotificaciones = (NotificationManager)
            getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builderNotificaciones = new NotificationCompat
            .Builder(this, "APK Euroliga");

        Bundle bundle = new Bundle();
        bundle.putInt("cantidadFavoritos", cantidadEquiposFavoritos);
        PendingIntent pendingIntent = new NavDeepLinkBuilder(getBaseContext())
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.listaPartidosFragment)
            .setArguments(bundle)
            .createPendingIntent();

        builderNotificaciones.setSmallIcon(R.drawable.logo_notificaciones_euroliga)
            .setContentTitle(getString(R.string.notificacion_titulo))
            .setContentText(getString(R.string.notificacion_texto, equipoFavorito))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canalNotificaciones = new NotificationChannel(
                "APK Euroliga", getString(R.string.notificacion_grande_titulo), NotificationManager.IMPORTANCE_DEFAULT
            );
            managerNotificaciones.createNotificationChannel(canalNotificaciones);

            canalNotificaciones.setDescription("APK Euroliga");
            canalNotificaciones.enableLights(true);
            canalNotificaciones.setLightColor(Color.RED);

            managerNotificaciones.notify(idNotificacion, builderNotificaciones.build());
        }
    }

    @Override
    public void borrarNotificaciones(int cantidadFavoritos) {
        NotificationManager managerNotificaciones = (NotificationManager)
            getSystemService(Context.NOTIFICATION_SERVICE);
        for (int idNot = 1; idNot <= cantidadFavoritos; idNot++) {
            managerNotificaciones.cancel(idNot);
        }
    }
}