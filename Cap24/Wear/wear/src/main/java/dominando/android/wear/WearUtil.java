package dominando.android.wear;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import java.util.List;
public class WearUtil implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private List<Node> mNodes;
    private GoogleApiClient mGoogleApiClient;
    public WearUtil(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    public void conectar() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }
    public void desconectar() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    public void enviarMensagem(String path, byte[] msg) {
        if (mNodes != null && mGoogleApiClient.isConnected()) {
            for (Node node: mNodes) {
                Wearable.MessageApi.sendMessage(
                        mGoogleApiClient, node.getId(), path, msg);
            }
        }
    }
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
    @Override
    public void onConnected(Bundle bundle) {
        descobrirNoDestino();
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    private void descobrirNoDestino() {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult
                                                 getConnectedNodesResult) {
                        mNodes = getConnectedNodesResult.getNodes();
                    }
                }
        );
    }
}

