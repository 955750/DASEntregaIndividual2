package com.example.dasentregaindividual2.ajustes;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.dasentregaindividual2.R;

public class PreferenciasFragment extends PreferenceFragmentCompat
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.pref_config);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferencias = getPreferenceManager().getSharedPreferences();
        if (preferencias != null)
            preferencias.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences preferencias = getPreferenceManager().getSharedPreferences();
        if (preferencias != null)
            preferencias.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        SharedPreferences preferencias = PreferenceManager
            .getDefaultSharedPreferences(requireContext());
        switch (s) {
            case "idioma":
                String idioma = preferencias.getString("idioma", null);
                switch (idioma) {
                    case "Euskara":
                        cambiarIdioma("eu");
                        break;
                    case "Castellano":
                        cambiarIdioma("es");
                        break;
                    case "English":
                        cambiarIdioma("en");
                        break;
                }
                break;
            case "modo_oscuro":
                boolean modoOscuro = preferencias.getBoolean("modo_oscuro", false);
                cambiarModo(modoOscuro);
                break;
            default:
                break;
        }
    }

    private void cambiarModo(boolean pModoOscuro) {
        if (pModoOscuro)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    /*
     * Función para cambiar el idioma. Al tener sólo una actividad, si esta mata y se vuelve a
     * crear se vuelve a la pantalla del login, por lo que se ha buscado una alternativa para el
     * cambio de idioma basándose en el siguente enlace:
     * https://developer.android.com/about/versions/13/features/app-languages?hl=es-419
     */
    private void cambiarIdioma(String pIdioma) {
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(pIdioma);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
}