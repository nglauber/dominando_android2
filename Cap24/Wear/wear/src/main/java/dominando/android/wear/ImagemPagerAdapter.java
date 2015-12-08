package dominando.android.wear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
public class ImagemPagerAdapter extends FragmentGridPagerAdapter {
    private int mCount;
    public ImagemPagerAdapter(FragmentManager fm, int count) {
        super(fm);
        mCount = count;
    }
    @Override
    public Fragment getFragment(int row, int column) {
        CardFragment cardFragment = CardFragment.create("Passando Imagens",
                String.format("%d/%d", column + 1, mCount));
        return cardFragment;
    }
    @Override
    public int getRowCount() {
        return 1;
    }
    @Override
    public int getColumnCount(int row) {
        return mCount;
    }
}

