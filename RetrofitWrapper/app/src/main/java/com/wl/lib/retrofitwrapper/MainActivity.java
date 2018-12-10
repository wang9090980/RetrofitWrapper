package com.wl.lib.retrofitwrapper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wl.devlib.http.RetrofitHelper;
import com.wl.devlib.http.RetrofitManager;
import com.wl.devlib.http.exception.HttpException;
import com.wl.devlib.http.model.BaseEntity;
import com.wl.devlib.http.rxjava.BaseObserver;
import com.wl.devlib.http.rxjava.HttpObserver;
import com.wl.devlib.http.rxjava.RxJavaHelper;
import com.wl.devlib.http.rxjava.subscribe.EasySubscriber;
import com.wl.devlib.http.rxjava.transformer.ObservableTransformerAsync;
import com.wl.devlib.http.rxjava.transformer.ObservableTransformerError;
import com.wl.devlib.http.utils.LoggerUtils;
import com.wl.lib.retrofitwrapper.entity.CityInfo;
import com.wl.lib.retrofitwrapper.entity.News;
import com.wl.lib.retrofitwrapper.entity.NewsWrapper;
import com.wl.lib.retrofitwrapper.service.APPApiService;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_weather;
    private Button btn_login;
    private Button btn_download;
    private Button btn_upload;
    private TextView tv_show;
    private Button btn_fastjson;


    private CompositeDisposable mCompositeDisposable; //订阅事件管理器

    protected CompositeDisposable getCompositeDisposable() {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        return mCompositeDisposable;
    }

    public void unsubscribe() {
        getCompositeDisposable().dispose();
        mCompositeDisposable = null;
    }


    /**
     * 订阅(异步)
     *
     * @param <T> 观察项目类型
     * @param observable 被观察者
     * @param observer 观察者
     */
    public <T> Disposable subscribe(Observable<T> observable, final BaseObserver<T> observer) {
        observable = observable
                .compose(new ObservableTransformerAsync<T>())
                .compose(new ObservableTransformerError<T>());
        Disposable disposable = RxJavaHelper.subscribe(observable, observer);// 建立订阅关系
        add(disposable);
        return disposable;
    }

    public boolean add(@io.reactivex.annotations.NonNull Disposable disposable) {
        return getCompositeDisposable().add(disposable);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_weather = findViewById(R.id.btn_weather);
        btn_login = findViewById(R.id.btn_login);
        btn_download = findViewById(R.id.btn_download);
        btn_upload = findViewById(R.id.btn_upload);
        tv_show = findViewById(R.id.tv_show);
        btn_fastjson = findViewById(R.id.btn_fastjson);


        btn_weather.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_download.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        btn_fastjson.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_weather:
                getWeatherStr();
                //getWeatherModel();
                break;
            case R.id.btn_login:
                doLogin();
                break;
            case R.id.btn_download:
                break;
            case R.id.btn_upload:
                break;
            case R.id.btn_fastjson:
                testFastJson();
                break;
            default:
                break;
        }

    }


    private void testFastJson() {
        RetrofitHelper
                .createService(APPApiService.class)
                .getNews(1, 50)
                .compose(RetrofitHelper.<NewsWrapper>handleResult())
                .subscribe(new EasySubscriber<NewsWrapper>(this) {
                    @Override
                    protected void _onNext(NewsWrapper wrapper) {
                        List<News> dataList = wrapper.getDataList();
                        if (dataList != null && !dataList.isEmpty()) {
                            News news = dataList.get(0);
                            tv_show.setText("第一条测试数据：" + news.getContent());
                        }

                    }

                    @Override
                    protected void _onError(int errorCode, String msg) {

                    }
                });


    }


    private void doLogin() {
        RetrofitHelper.createService(APPApiService.class)
                .login("123", "456")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EasySubscriber<BaseEntity>(this) {
                    @Override
                    protected void _onNext(BaseEntity baseEntity) {
                        Log.i("retrofit", "onNext=========>" + baseEntity.getCode());

                    }

                    @Override
                    protected void _onError(int errorCode, String msg) {

                    }
                });
    }


    private void getWeatherModel() {

        RetrofitHelper
                .createService(APPApiService.class)
                .getCityInfoModel()
                .compose(RetrofitHelper.<CityInfo>handleResult())
                .subscribe(new EasySubscriber<CityInfo>(this) {

                    @Override
                    protected void _onNext(CityInfo userBean) {
                        Log.i("retrofit", "onNext=========>" + userBean.getCityName());
                    }

                    @Override
                    protected void _onError(int errorCode, String msg) {
                        Log.i("retrofit", "onError=========>" + msg);
                    }
                });


    }


    private void getWeatherStr() {
        subscribe(RetrofitManager.getInstance().getRetrofit().create(APPApiService.class).getCityInfo(),
                new HttpObserver<ResponseBody>(this) {
                    @Override
                    public void _onNext(ResponseBody responseBody) {
                        try {
                            String json = responseBody.string();
                            Log.d("_onNext", json);

                            tv_show.setText("返回数据：" + json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void _onError(HttpException exception) {
                        LoggerUtils.e(exception.getDesc());
                    }
                });
    }


}
