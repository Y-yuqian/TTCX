package com.example.baidumap;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.SupplementMode;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.ProtocolType;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TransportMode;
import com.example.baidumap.MyOrientationListener.OnOrientationListener;
import com.example.util.BottomDialogUtil;
import com.example.util.Info;
import com.example.util.MapUtil;
import com.sdust.im.R;

/**
 * 地图碎片
 * 
 * @author Diviner
 * @date 2018-1-19 下午3:26:37
 */
public class MapFragment extends Fragment implements OnMarkerClickListener,
		OnMapClickListener, OnClickListener {
	private Context mContext;// 当前上下文

	// 地图相关
	private MapView mMapView; // 成员变量对应着地图主控件
	private BaiduMap mBaiduMap;

	// 定位相关
	private LocationClient mLocationClient;// 定位的核心api
	private MyLocationListener mLocationListener;// 定位的监听器
	private boolean mInFirstIn = true;// 用于判断是否为第一次使用
	private double mLatitube;// 纬度
	private double mLongitube;// 经度
	private LocationMode mLocationMode;// 模式

	// 自定义定位图标相关
	private BitmapDescriptor mIconLocation;

	// 方向传感器相关
	private MyOrientationListener mMyOrientationListener;
	private float mCurrenX;

	// 覆盖物相关
	private BitmapDescriptor mMarker;
	private RelativeLayout mMarkerLayout;// 布局
	private LinearLayout mLinearLayout;
	private Button mTrajectory;
	private boolean mTrajectoryOffAndOn = true;

	// 鹰眼相关
	private long serviceId = 151352;// 轨迹服务ID
	private String entityName = "MyVm";// 设备标识
	// 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK
	// v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
	private boolean isNeedObjectStorage = false;// 通常设置为false不使用
	private LBSTraceClient mLBSTraceClient;// 初始化轨迹服务客户端
	private Trace mTrace;// 初始化轨迹服务
	private OnTraceListener mTraceListener;
	private OnTrackListener mTrackListener;
	private HistoryTrackRequest mHistoryTrackRequest;// 创建轨迹实例
	private List<LatLng> trackPoints = new ArrayList<LatLng>();// 轨迹点集合
	private SortType sortType = SortType.asc;// 轨迹排序规则
	public static final int PAGE_SIZE = 5000;
	private int pageIndex = 1;

	public BitmapDescriptor bmStart;
	public BitmapDescriptor bmEnd;
	public BitmapDescriptor bmArrowPoint;

	// 路线覆盖物
	public Overlay polylineOverlay;
	private Marker mMoveMarker;
	private MapStatus mapStatus;

	// 控件按钮相关
	private ImageView mSynthesis;
	private ImageView mQueryLine;
	private ImageView mMapModel;
	private int mIndex = 1;// 简单判断图片

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getActivity().getApplicationContext());
		View view = inflater
				.inflate(R.layout.map_home_layout, container, false);

		mContext = getActivity();// 初始化当前上下文

		initControl(view);// 初始化控件
		initLocation();// 初始化定位
		initMarkers(view);// 初始化覆盖物

		setHasOptionsMenu(true);// 显示menu菜单
		return view;
	}

	/**
	 * 该方法用于初始化控件
	 */
	public void initControl(View view) {
		// 加载这个控件
		mMapView = (MapView) view.findViewById(R.id.map_view_Id);
		mMapView.getChildAt(0).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				/*
				 * 父控件不截取子控件的事件 交给子控件自己处理
				 */
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:// 手势滑动View
					// 当手势滑动这个控件的时候交给它自己来处理这个事件
					mMapView.requestDisallowInterceptTouchEvent(true);
					break;
				case MotionEvent.ACTION_UP:// 手势抬起View（与DOWN对应）
				case MotionEvent.ACTION_CANCEL:// 非人为原因结束本次事件
					mMapView.requestDisallowInterceptTouchEvent(false);
					break;
				}
				return false;
			}
		});

		mBaiduMap = mMapView.getMap();// 得到当前地图，使当前的操作都是在已经打开的地图上操作

		mTrace = new Trace(serviceId, entityName, isNeedObjectStorage);// 初始化轨迹服务
		mLBSTraceClient = new LBSTraceClient(getActivity()
				.getApplicationContext());// 初始化轨迹服务客户端
		// 请求标识
		int tag = 1;
		// 创建历史轨迹请求实例
		mHistoryTrackRequest = new HistoryTrackRequest(tag, serviceId,
				entityName);

		mBaiduMap.setOnMarkerClickListener(this);// 设置点击事件
		mBaiduMap.setOnMapClickListener(this);// 点击事件

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);// 设置地图的缩放比例
		mBaiduMap.setMapStatus(msu);// 更新

		/*
		 * 初始化地图上的按钮
		 */
		mLinearLayout = (LinearLayout) view
				.findViewById(R.id.LinearLayout_button_id);
		mLinearLayout.setVisibility(View.VISIBLE);// 显示布局

		mTrajectory = (Button) view.findViewById(R.id.trajectory_id);
		if (mTrajectory.isClickable()) {// 是否点击
			mTrajectory.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						mTrajectory.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (mTrajectoryOffAndOn) {
									startTrack();// 开启鹰眼
									mLBSTraceClient.startTrace(mTrace,
											mTraceListener);// 开启轨迹服务
									mLBSTraceClient.startGather(mTraceListener);// 开启采集

									mTrajectory
											.setText(getString(R.string.menu_trajectory_on_string));// 设置为关闭
									mTrajectoryOffAndOn = false;

								} else {
									// 停止服务
									mLBSTraceClient.stopTrace(mTrace,
											mTraceListener);
									mLBSTraceClient.stopGather(mTraceListener);// 停止采集

									// 清除点和线
									mBaiduMap.clear();

									mTrajectory
											.setText(getString(R.string.menu_trajectory_off_string));// 设置为开启
									mTrajectoryOffAndOn = true;
								}

							}
						});
						break;
					}
					return false;
				}
			});
			mTrajectory.setOnClickListener(this);
		}

		mSynthesis = (ImageView) view.findViewById(R.id.Synthesis_id);
		mSynthesis.setOnClickListener(this);

		mQueryLine = (ImageView) view.findViewById(R.id.queryLine_id);
		mQueryLine.setOnClickListener(this);

		mMapModel = (ImageView) view.findViewById(R.id.map_model_id);
		mMapModel.setOnClickListener(this);
	}

	/**
	 * 初始化定位信息
	 */
	public void initLocation() {
		mLocationMode = LocationMode.NORMAL;// 默认为普通默认

		mLocationClient = new LocationClient(getActivity());

		mLocationListener = new MyLocationListener();// 实例化这个监听器

		mLocationClient.registerLocationListener(mLocationListener);// 注册监听器

		// 设置一些必要的配置
		LocationClientOption option = new LocationClientOption();// 定位参数
		option.setCoorType("bd09ll");// 定义当前坐标类型
		option.setIsNeedAddress(true);// 返回当前位置
		option.setOpenGps(true);// 打开GPS定位
		option.setScanSpan(5000);// 5000毫秒--5秒

		mLocationClient.setLocOption(option);// 如果不设置该参数可能导致以上配置不生效的问题

		// 初始化图标
		mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.up);// 选择一个图标R.drawable.up

		mMyOrientationListener = new MyOrientationListener(mContext);// 实例化方向传感器
		mMyOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {

					@Override
					public void OrientationChanged(float x) {
						// 当得到方向之后更新地图上方向图标的位置
						mCurrenX = x;
					}
				});
	}

	/**
	 * 初始化覆盖物
	 */
	private void initMarkers(View view) {
		mMarker = BitmapDescriptorFactory.fromResource(R.drawable.location);
		mMarkerLayout = (RelativeLayout) view.findViewById(R.id.id_marker_ly);// 初始化布局
	}

	/**
	 * 开启鹰眼
	 */
	public void startTrack() {
		// 实例化开启轨迹服务回调接口
		// 初始化轨迹服务监听器
		mTraceListener = new OnTraceListener() {

			// 开启服务回调
			@Override
			public void onStartTraceCallback(int status, String message) {
				Log.d("TrackRecord", "StartTrace:成功" + status + "message:"
						+ message);
			}

			// 停止服务回调
			@Override
			public void onStopTraceCallback(int status, String message) {
				Log.d("TrackRecord", "StopTrace:成功" + status + "message:"
						+ message);
			}

			// 开启采集回调
			@Override
			public void onStartGatherCallback(int status, String message) {
				Log.d("TrackRecord", "StartGather:成功" + status + "message:"
						+ message);
			}

			// 停止采集回调
			@Override
			public void onStopGatherCallback(int status, String message) {
				Log.d("TrackRecord", "StopGather:成功" + status + "message:"
						+ message);
			}

			// 推送回调
			@Override
			public void onPushCallback(byte messageNo, PushMessage message) {
			}

			@Override
			public void onBindServiceCallback(int arg0, String arg1) {
			}

			@Override
			public void onInitBOSCallback(int arg0, String arg1) {
			}
		};
		int gatherInterval = 5; // 位置采集周期
		int packInterval = 15;// 打包周期
		mLBSTraceClient.setInterval(gatherInterval, packInterval);// 设置位置采集和打包周期
		mLBSTraceClient.setProtocolType(ProtocolType.HTTP);// 设置协议类型，0为http，1为https

		queryTrajectory();
	}

	/**
	 * 鹰眼查询实时位置
	 */
	public void queryTrajectory() {
		// 设置轨迹查询起止时间
		long startTime = System.currentTimeMillis() / 1000 - 12 * 60 * 60;// 开始时间(单位：秒)
		long endTime = System.currentTimeMillis() / 1000;// 结束时间(单位：秒)

		mHistoryTrackRequest.setStartTime(startTime);// 设置开始时间
		mHistoryTrackRequest.setEndTime(endTime);// 设置结束时间
		mHistoryTrackRequest.setProcessed(true);// 设置开启纠正偏差

		// 创建纠偏选项实例
		ProcessOption processOption = new ProcessOption();// 纠正需要设置的参数
		processOption.setNeedDenoise(true);// 设置需要去噪
		processOption.setNeedVacuate(true);// 设置需要抽稀
		processOption.setNeedMapMatch(true);// 设置需要绑路
		processOption.setRadiusThreshold(100);// 设置精度过滤值(定位精度大于100米的过滤掉)
		processOption.setTransportMode(TransportMode.walking);// 设置为步行

		mHistoryTrackRequest.setProcessOption(processOption);// 设置纠偏选项
		// 设置里程填充方式为步行
		mHistoryTrackRequest.setSupplementMode(SupplementMode.walking);

		// 初始化轨迹监听器
		mTrackListener = new OnTrackListener() {

			// 历史轨迹回调
			@Override
			public void onHistoryTrackCallback(HistoryTrackResponse response) {
				int toal = response.getTotal();
				if (StatusCodes.SUCCESS != response.getStatus()) {
					Toast.makeText(mContext, response.getMessage(),
							Toast.LENGTH_LONG);
				} else if (0 == toal) {
					Toast.makeText(mContext, "未查到轨迹", Toast.LENGTH_LONG);
				} else {
					List<TrackPoint> points = response.getTrackPoints();
					if (null != points) {
						for (TrackPoint trackPoint : points) {
							if (!MapUtil.isZeroPoint(trackPoint.getLocation()
									.getLatitude(), trackPoint.getLocation()
									.getLongitude())) {
								trackPoints.add(new LatLng(trackPoint
										.getLocation().latitude, trackPoint
										.getLocation().longitude));
							}
						}
					}
				}
				if (toal > PAGE_SIZE * pageIndex) {
					mHistoryTrackRequest.setPageIndex(++pageIndex);
				} else {
					drawHistoryTrack(trackPoints, sortType);
				}

				// Log.d("TrackRecord", "EntityName:" + response.getEntityName()
				// + "-Message:" + response.getMessage() + "toal:" + toal);
				// 查询历史轨迹
				queryTrajectory();
			}
		};
		mLBSTraceClient.queryHistoryTrack(mHistoryTrackRequest, mTrackListener);
	}

	/**
	 * 绘制历史轨迹
	 */
	public void drawHistoryTrack(final List<LatLng> points, SortType sortType) {
		// 绘制新覆盖物前，清空之前的覆盖物
		mBaiduMap.clear();
		if (null != mMoveMarker) {
			mMoveMarker.remove();
			mMoveMarker = null;
		}
		if (points == null || points.size() == 0) {
			if (null != polylineOverlay) {
				polylineOverlay.remove();
				polylineOverlay = null;
			}
			return;
		}

		// 绘制起点终点图标
		bmStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);// 设置起点图标
		bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.down);// 设置终点图标
		bmArrowPoint = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_end);

		if (points.size() == 1) {// 只有一个点的情况
			OverlayOptions startOptions = new MarkerOptions()
					.position(points.get(0)).icon(bmStart).zIndex(9)
					.draggable(true);
			mBaiduMap.addOverlay(startOptions);

			animateMapStatus(points.get(0), 18.0f);
			return;
		}

		// 起点终点经纬度
		LatLng startPoint;
		LatLng endPoint;
		if (sortType == SortType.asc) {
			startPoint = points.get(0);
			endPoint = points.get(points.size() - 1);
		} else {
			startPoint = points.get(points.size() - 1);
			endPoint = points.get(0);
		}

		// 添加起点图标
		OverlayOptions startOptions = new MarkerOptions().position(startPoint)
				.icon(bmStart).zIndex(9).draggable(true);
		mBaiduMap.addOverlay(startOptions);// 添加起点

		// 添加终点图标
		OverlayOptions endOptions = new MarkerOptions().position(endPoint)
				.icon(bmEnd).zIndex(9).draggable(true);
		mBaiduMap.addOverlay(endOptions);// 添加终点

		// 添加路线（轨迹）
		OverlayOptions polylineOptions = new PolylineOptions().width(10)
				.color(Color.BLUE).points(points);// 用蓝色的线来表示
		polylineOverlay = mBaiduMap.addOverlay(polylineOptions);// 添加路线

		OverlayOptions markerOptions = new MarkerOptions()//
				.flat(true)//
				.anchor(0.5f, 0.5f)//
				.icon(bmArrowPoint)//
				.position(points.get(points.size() - 1))//
				.rotate((float) MapUtil.getAngle(points.get(0), points.get(1)));

		mMoveMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
		animateMapStatus(points);
	}

	public void animateMapStatus(LatLng point, float zoom) {
		MapStatus.Builder builder = new MapStatus.Builder();
		mapStatus = builder.target(point).zoom(zoom).build();
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory
				.newMapStatus(mapStatus));
	}

	public void animateMapStatus(List<LatLng> points) {
		if (null == points || points.isEmpty()) {
			return;
		}
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (LatLng point : points) {
			builder.include(point);
		}
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngBounds(builder
				.build());
		mBaiduMap.animateMapStatus(msu);// 更新地图
	}

	@Override
	public void onClick(View v) {
		/*
		 * 所有按钮的点击事件
		 */

		switch (v.getId()) {
		case R.id.map_model_id:
			if (mIndex == 0) {
				mMapModel.setImageResource(R.drawable.left_jpg0);
				mLocationMode = LocationMode.NORMAL;
				mIndex++;
			} else if (mIndex == 1) {
				mMapModel.setImageResource(R.drawable.left_jpg1);
				mLocationMode = LocationMode.FOLLOWING;
				mIndex++;
			} else if (mIndex == 2) {
				mMapModel.setImageResource(R.drawable.left_jpg2);
				mLocationMode = LocationMode.COMPASS;
				mIndex = 0;
			}
			break;
		case R.id.Synthesis_id:// 综合按钮
			new BottomDialogUtil(getActivity()) {

				@Override
				public void GeneralMap() {
					/*
					 * 普通地图逻辑
					 */
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);// 设置地图类型为普通地图
				}

				@Override
				public void SatelliteMap() {
					/*
					 * 卫星地图逻辑
					 */
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);// 设置地图类型为卫星地图
				}

				@Override
				public void RealTimeTraffic() {
					/*
					 * 实时交通逻辑
					 */
					if (mBaiduMap.isTrafficEnabled()) {// 首先判断是否已经显示了实时交通
						mBaiduMap.setTrafficEnabled(false);// 如果已经打开则关闭它
					} else {
						mBaiduMap.setTrafficEnabled(true);
					}
				}

			}.show();// 显示
			break;
		case R.id.queryLine_id:
			break;
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// 地图的点击事件
		mMarkerLayout.setVisibility(View.GONE);// 隐藏布局
		mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Bundle bundleInfo = marker.getExtraInfo();// 得到传过来bundle的数据
		Info info = (Info) bundleInfo.getSerializable("info");// 传入键值去到相应的数据

		/*
		 * mMarkerLayout通过大布局取到其中的东西
		 */
		ImageView imageView = (ImageView) mMarkerLayout
				.findViewById(R.id.id_info_img);
		TextView distence = (TextView) mMarkerLayout
				.findViewById(R.id.id_info_distance);
		TextView name = (TextView) mMarkerLayout
				.findViewById(R.id.id_info_name);
		TextView zan = (TextView) mMarkerLayout.findViewById(R.id.id_info_zan);

		// 数据填充
		imageView.setImageResource(info.getImageId());
		distence.setText(info.getDistance());
		name.setText(info.getName());
		int zans = info.getZan();
		zan.setText(String.valueOf(zans));

		InfoWindow infoWindow;

		TextView tv = new TextView(mContext);// 在当前活动上创建一个文本框
		tv.setBackground(getResources().getDrawable(R.drawable.img_bottom));
		tv.setPadding(30, 20, 30, 50);
		tv.setText(info.getName());

		final LatLng latLng = marker.getPosition();// 获取经纬度
		Point point = mBaiduMap.getProjection().toScreenLocation(latLng);// 把经纬度转换成普通的点
		point.y -= 47;

		LatLng ll = mBaiduMap.getProjection().fromScreenLocation(point);

		infoWindow = new InfoWindow(tv, ll, 0);

		mBaiduMap.showInfoWindow(infoWindow);// 显示这个窗口

		mMarkerLayout.setVisibility(View.VISIBLE);// 显示布局
		return true;
	}

	// @Override
	// public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// super.onCreateOptionsMenu(menu, inflater);
	// inflater.inflate(R.menu.main, menu);
	//
	// }

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// /*
	// * 菜单的按钮的点击事情
	// */
	// switch (item.getItemId()) {// 根据点击的菜单id来进行判断
	// case R.id.Menu_GeneralMapId:// 普通地图
	// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);// 设置地图类型为普通地图
	// break;
	// case R.id.Menu_SatelliteMapId:// 卫星地图
	// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);// 设置地图类型为卫星地图
	// break;
	// case R.id.Menu_RealTime_trafficId:// 设置地图类型为实时交通地图
	// if (mBaiduMap.isTrafficEnabled()) {// 首先判断是否已经显示了实时交通
	// mBaiduMap.setTrafficEnabled(false);// 如果已经打开则关闭它
	// item.setTitle(getString(R.string.menu_real_time_traffic_off_string));
	// } else {
	// mBaiduMap.setTrafficEnabled(true);
	// item.setTitle(getString(R.string.menu_real_time_traffic_on_string));
	// }
	// break;
	// case R.id.Menu_My_AddressId:// 我的位置
	// centerToMyAddress();
	// break;
	// case R.id.Menu_normalModeId:// 普通模式
	// mLocationMode = LocationMode.NORMAL;
	// break;
	// case R.id.Menu_followModeId:// 跟随模式
	// mLocationMode = LocationMode.FOLLOWING;
	// break;
	// case R.id.Menu_compassModeId:// 罗盘模式
	// mLocationMode = LocationMode.COMPASS;
	// break;
	// case R.id.Menu_addObstacleId:// 添加覆盖物
	// addObstacle(Info.infos);
	// break;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	/**
	 * 添加覆盖物
	 * 
	 * @param infos
	 */
	private void addObstacle(List<Info> infos) {
		mBaiduMap.clear();// 清除一些不需要的图层

		LatLng latLng = null;// 用于接收经纬度

		Marker marker = null;
		OverlayOptions options;

		for (Info bl : infos) {
			// 为经纬度赋值
			latLng = new LatLng(bl.getLatitube(), bl.getLongitube());
			// 图标
			options = new MarkerOptions().position(latLng)// 设置在地图上的什么位置
					.icon(mMarker)// 图标
					.zIndex(5);// 5为地图最上层显示

			marker = (Marker) mBaiduMap.addOverlay(options);// 调用方法添加覆盖物

			Bundle bundle = new Bundle();// bundle对象用于数据传输
			bundle.putSerializable("info", bl);

			marker.setExtraInfo(bundle);
		}

		// 移动地图到当前位置
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);// 传入当前位置
		mBaiduMap.setMapStatus(msu);// 更新位置
	}

	/**
	 * 定位到我的位置
	 */
	public void centerToMyAddress() {
		LatLng latLng = new LatLng(mLatitube, mLongitube);// 传入当前经纬度

		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);// 传入当前位置
		mBaiduMap.animateMapStatus(msu);// 定位到当前的位置
	}

	/**
	 * 定位
	 * 
	 * @author 胡涂涂i
	 * 
	 */
	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			/*
			 * 当定位成功以后进行此方法的回调
			 */
			MyLocationData data = new MyLocationData.Builder()
					.direction(mCurrenX)// 当定位成功并获得方向传感器之后进行方向的设置
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())// 纬度
					.longitude(location.getLongitude())// 经度
					.build();

			mBaiduMap.setMyLocationData(data);// 将数据转换给地图对象

			/*
			 * 加载一个自定义图标,LocationMode.NORMAL正常模式
			 * LocationMode.NORMAL模式，mIconLocation图标对象
			 */
			MyLocationConfiguration config = new MyLocationConfiguration(
					mLocationMode, true, mIconLocation);
			mBaiduMap.setMyLocationConfiguration(config);// 将配置加载进来

			// 每次定位成功后将更新存放经纬度的变量
			mLatitube = location.getLatitude();// 把最新获取的数据存入变量
			mLongitube = location.getLongitude();// 把最新获取的数据存入变量

			if (mInFirstIn) {// 是否为第一次
				/*
				 * 只有第一次进来的时候会定位到用户的中心点,也就是所在位置
				 */
				LatLng latLng = new LatLng(location.getLatitude(),
						location.getLongitude());// 经度和纬度的方法
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);// 传入当前位置

				mBaiduMap.animateMapStatus(msu);

				mInFirstIn = false;// 第一次之后设置为false

				// Toast.makeText(mContext, "" + location.getAddrStr(),
				// Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
		// onResume执行之后执行这个方法,在此方法中开启定位
		mBaiduMap.setMyLocationEnabled(true);// 地图开启定位的允许

		if (!mLocationClient.isStarted()) {// 如果没有启动则启动定位
			mLocationClient.start();

			// 开启方向传感器
			mMyOrientationListener.start();// 跟活动的生命周期一致
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mBaiduMap.setMyLocationEnabled(false);// 关闭应用之后地图不允许开启定位
		mLocationClient.stop();// 停止定位
		mMyOrientationListener.stop();// 关闭方向传感器

		// 鹰眼 第一次以后下次使用可以不传监听器
		mLBSTraceClient.stopTrace(mTrace, mTraceListener);// 停止服务
		mLBSTraceClient.stopGather(mTraceListener);// 停止采集
		mLBSTraceClient.clear();// 清理资源
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}
}
