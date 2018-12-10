package com.wl.devlib.http;


import com.wl.devlib.http.download.DownLoadService;
import com.wl.devlib.http.download.DownLoadTransformer;
import com.wl.devlib.http.exception.ServerException;
import com.wl.devlib.http.model.BaseEntity;
import com.wl.devlib.http.rxjava.HttpObserver;
import com.wl.devlib.http.rxjava.RxJavaHelper;
import com.wl.devlib.http.rxjava.transformer.ObservableTransformerAsync;
import com.wl.devlib.http.rxjava.transformer.ObservableTransformerError;
import com.wl.devlib.http.rxjava.transformer.ObservableTransformerSync;
import com.wl.devlib.http.upload.UploadOnSubscribe;
import com.wl.devlib.http.upload.UploadRequestBody;
import com.wl.devlib.http.utils.FileUtils;

import org.reactivestreams.Publisher;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;


public class RetrofitHelper {


    /**
     * 订阅(同步)
     *
     * @param <T> 观察项目类型
     * @param observable 被观察者
     * @param observer 观察者
     */
    public <T> Disposable subscribeSync(Observable<T> observable, final HttpObserver<T> observer) {
        observable = observable.compose(new ObservableTransformerSync<T>())
                .compose(new ObservableTransformerError<T>());
        return RxJavaHelper.subscribe(observable, observer);// 建立订阅关系
    }

    /**
     * 订阅(异步)
     *
     * @param <T> 观察项目类型
     * @param observable 被观察者
     * @param observer 观察者
     */
    public <T> Disposable subscribeAsync(Observable<T> observable, final HttpObserver<T> observer) {
        observable = observable.compose(new ObservableTransformerAsync<T>())
                .compose(new ObservableTransformerError<T>());
        return RxJavaHelper.subscribe(observable, observer);// 建立订阅关系
    }


    /**
     * 创建请求
     */
    public static <T> T createService(final Class<T> service) {
        return RetrofitManager.getInstance().getRetrofit().create(service);
    }


    /**
     * 转换器
     * from BaseModel<T>
     * to T
     */
    public static <T> FlowableTransformer<BaseEntity<T>, T> handleResult() {
        return new FlowableTransformer<BaseEntity<T>, T>() {
            @Override
            public Publisher<T> apply(@NonNull Flowable<BaseEntity<T>> upstream) {
                return upstream.flatMap(new Function<BaseEntity<T>, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(@NonNull BaseEntity<T> tBaseModel) throws Exception {
                        if (tBaseModel!=null&&!tBaseModel.isFailure()) {
                            return createData(tBaseModel.getData());
                        } else {
                            return Flowable.error(new ServerException(tBaseModel.getDesc(), tBaseModel.getCode()));
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }




    /**
     * 创建Flowable<T>
     */
    public static <T> Flowable<T> createData(final T result) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<T> e) throws Exception {
                try {
                    e.onNext(result);
                    e.onComplete();
                } catch (Exception exception) {
                    e.onError(exception);
                }
            }
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * 上传单个文件
     */
    public <T> Flowable<Object> uploadFile(
            File file,
            Class<T> uploadServiceClass,
            String uploadFucntionName,
            Object... params) {
        //进度Observable
        UploadOnSubscribe uploadOnSubscribe = new UploadOnSubscribe(file.length());
        Flowable<Integer> progressObservale = Flowable.create(uploadOnSubscribe, BackpressureStrategy.BUFFER);

        UploadRequestBody uploadRequestBody = new UploadRequestBody(file);
        //设置进度监听
        uploadRequestBody.setUploadOnSubscribe(uploadOnSubscribe);


        //创建表单主体
        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("upload", file.getName(), uploadRequestBody);


        //上传
        T service = createService(uploadServiceClass);


        try {
            //获得上传方法的参数类型和参数
            Class[] paramClasses = new Class[params.length + 1];
            Object[] uploadParams = new Object[params.length + 1];
            paramClasses[params.length] = MultipartBody.Part.class;
            uploadParams[params.length] = filePart;
            for (int i = 0; i < params.length; i++) {
                paramClasses[i] = params[i].getClass();
                uploadParams[i] = params[i];
            }

            //获得上传方法
            Method uploadMethod = uploadServiceClass.getMethod(uploadFucntionName, paramClasses);

            //运行上传方法
            Object o = uploadMethod.invoke(service, uploadParams);
            if (o instanceof Flowable) {
                Flowable uploadFlowable = (Flowable) o;

                //合并Observable
                return Flowable.merge(progressObservale, uploadFlowable)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Flowable.error(e);
        }
        return Flowable.error(
                new ServerException("no upload method found or api service error", ServerException.ERROR_OTHER));
    }

    /**
     * 上传多个文件
     */
    public <T> Flowable<Object> uploadFiles(
            ArrayList<File> files,
            Class<T> uploadsServiceClass,
            String uploadFucntionName,
            Object... params) {
        //总长度
        long sumLength = 0l;
        for (File file : files) {
            sumLength += file.length();
        }

        //进度Observable
        UploadOnSubscribe uploadOnSubscribe = new UploadOnSubscribe(sumLength);
        Flowable<Integer> progressObservale = Flowable.create(uploadOnSubscribe, BackpressureStrategy.BUFFER);

        ArrayList<MultipartBody.Part> fileParts = new ArrayList<>();

        for (File file : files) {

            UploadRequestBody uploadRequestBody = new UploadRequestBody(file);
            //设置进度监听
            uploadRequestBody.setUploadOnSubscribe(uploadOnSubscribe);

            fileParts.add(MultipartBody.Part.createFormData("upload", file.getName(), uploadRequestBody));
        }

        //上传
        T service = createService(uploadsServiceClass);

        try {
            //获得上传方法的参数类型   和参数
            Class[] paramClasses = new Class[params.length + 1];
            Object[] uploadParams = new Object[params.length + 1];
            paramClasses[params.length] = ArrayList.class;
            uploadParams[params.length] = fileParts;
            for (int i = 0; i < params.length; i++) {
                paramClasses[i] = params[i].getClass();
                uploadParams[i] = params[i];
            }

            //获得上传方法
            Method uploadMethod = uploadsServiceClass.getMethod(uploadFucntionName, paramClasses);

            //运行上传方法
            Object o = uploadMethod.invoke(service, uploadParams);
            if (o instanceof Flowable) {
                Flowable uploadFlowable = (Flowable) o;

                //合并Observable
                return Flowable.merge(progressObservale, uploadFlowable)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Flowable.error(e);
        }
        return Flowable.error(
                new ServerException("no upload method found or api service error", ServerException.ERROR_OTHER));
    }

    /**
     * 下载文件
     */
    public Flowable<Object> downLoadFile(String url) {
        return downLoadFile(url, null, null);
    }

    /**
     * 下载文件
     */
    public Flowable<Object> downLoadFile(String url, String savePath, String fileName) {

        if (savePath == null || savePath.trim().equals("")) {
            savePath = FileUtils.getDefaultDownLoadPath();
        }
        if (fileName == null || fileName.trim().equals("")) {
            fileName = FileUtils.getDefaultDownLoadFileName(url);
        }

        //下载监听
        DownLoadTransformer downLoadTransformer = new DownLoadTransformer(savePath, fileName);

        return Flowable
                .just(url)
                .flatMap(new Function<String, Publisher<ResponseBody>>() {
                    @Override
                    public Publisher<ResponseBody> apply(@NonNull String s) throws Exception {
                        DownLoadService downLoadService = createService(DownLoadService.class);
                        return downLoadService.startDownLoad(s);
                    }
                })
                .compose(downLoadTransformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
