the album library base on RxGalleryFinal

支持用RxJava的方式回调crop之后的图片

============================================

RxGalleryFinal
                .with(MainActivity.this)
                .image()
                .radio()
                .cropAspectRatioOptions(0, new AspectRatio("裁切头像", 1, 1))
                .crop()
                .imageLoader(ImageLoaderType.FRESCO)
                .subscribeCropListener(new RxBusResultDisposable<ImageCropResultEvent>() {
                    @Override
                    protected void onEvent(ImageCropResultEvent baseResultEvent) throws Exception {
                        Glide.with(getApplicationContext()).load(new File(baseResultEvent.getCropPath())).into(ivPicture);
                    }
                })  //回调裁切后的图片
                .subscribeGalleryListener(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        Toast.makeText(getBaseContext(), "选中了图片路径：" + imageRadioResultEvent.getResult().getOriginalPath(), Toast.LENGTH_SHORT).show();
                    }
                })
                .openGallery();






=============================================