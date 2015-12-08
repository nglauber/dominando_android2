package dominando.android.wearui;

import android.content.Context;
import android.graphics.Color;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
public class MeuListAdapter extends WearableListView.Adapter {
    private String[] mItens;
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean mAmbient;
    public MeuListAdapter(Context ctx, String[] itens) {
        mContext = ctx;
        mInflater = LayoutInflater.from(mContext);
        mItens = itens;
    }

    public void setAmbient(boolean ambient) {
        this.mAmbient = ambient;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mInflater.inflate(R.layout.item_lista, null));
    }
    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
        ItemViewHolder holder = (ItemViewHolder)viewHolder;
        holder.mTextView.setText(mItens[i]);
        holder.mImageView.setImageResource(R.mipmap.ic_launcher);
        holder.mImageView.setCircleRadius(40);

        holder.mTextView.setTextColor(mAmbient ? Color.WHITE : Color.BLACK);
        holder.mImageView.setVisibility(mAmbient ? View.GONE : View.VISIBLE);
    }
    @Override
    public int getItemCount() {
        return mItens.length;
    }
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        public TextView mTextView;
        public CircledImageView mImageView;
        public ItemViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.txtTexto);
            mImageView = (CircledImageView) v.findViewById(R.id.imgIcone);
        }
    }
}

