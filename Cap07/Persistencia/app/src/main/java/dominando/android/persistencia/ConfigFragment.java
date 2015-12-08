package dominando.android.persistencia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class ConfigFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener{

    EditTextPreference mPrefCidade;
    ListPreference mPrefRedeSocial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
        mPrefCidade = (EditTextPreference)
                findPreference(getString(R.string.pref_cidade));
        mPrefRedeSocial = (ListPreference)
                findPreference(getString(R.string.pref_rede_social));
        preencherSumario(mPrefCidade);
        preencherSumario(mPrefRedeSocial);
    }

    private void preencherSumario(Preference preference){
        preference.setOnPreferenceChangeListener(this);
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        Object value = pref.getString(preference.getKey(), "");
        onPreferenceChange(preference, value);
    }

    @Override
    public boolean onPreferenceChange(
            Preference preference, Object newValue) {
        String stringValue = newValue.toString();
        if (preference.equals(mPrefRedeSocial)){
            int index = mPrefRedeSocial.findIndexOfValue(stringValue);
            if (index >= 0){
                mPrefRedeSocial.setSummary(
                        mPrefRedeSocial.getEntries()[index]);
            }
        } else if (preference.equals(mPrefCidade)){
            mPrefCidade.setSummary(stringValue);
        }
        return true;
    }
}

