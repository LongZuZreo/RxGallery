package cn.finalteam.rxgalleryfinal.ui;

import cn.finalteam.rxgalleryfinal.ui.adapter.MediaGridAdapter;
import cn.finalteam.rxgalleryfinal.ui.base.IMultiImageCheckedListener;
import cn.finalteam.rxgalleryfinal.ui.base.IRadioImageCheckedListener;
import cn.finalteam.rxgalleryfinal.ui.fragment.MediaGridFragment;

/**
 * 处理回调监听
 * Created by KARL on 2017-03-17 04-42-25.
 */
public class RxGalleryListener {

    private static final class RxGalleryListenerHolder {
        private static final RxGalleryListener RX_GALLERY_LISTENER = new RxGalleryListener();
    }

    public static RxGalleryListener getInstance() {
        return RxGalleryListenerHolder.RX_GALLERY_LISTENER;
    }


}
