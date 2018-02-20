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
 * ��ͼ��Ƭ
 * 
 * @author Diviner
 * @date 2018-1-19 ����3:26:37
 */
public class MapFragment extends Fragment implements OnMarkerClickListener,
		OnMapClickListener, OnClickListener {
	private Context mContext;// ��ǰ������

	// ��ͼ���
	private MapView mMapView; // ��Ա������Ӧ�ŵ�ͼ���ؼ�
	private BaiduMap mBaiduMap;

	// ��λ���
	private LocationClient mLocationClient;// ��λ�ĺ���api
	private MyLocationListener mLocationListener;// ��λ�ļ�����
	private boolean mInFirstIn = true;// �����ж��Ƿ�Ϊ��һ��ʹ��
	private double mLatitube;// γ��
	private double mLongitube;// ����
	private LocationMode mLocationMode;// ģʽ

	// �Զ��嶨λͼ�����
	private BitmapDescriptor mIconLocation;

	// ���򴫸������
	private MyOrientationListener mMyOrientationListener;
	private float mCurrenX;

	// ���������
	private BitmapDescriptor mMarker;
	private RelativeLayout mMarkerLayout;// ����
	private LinearLayout mLinearLayout;
	private Button mTrajectory;
	private boolean mTrajectoryOffAndOn = true;

	// ӥ�����
	private long serviceId = 151352;// �켣����ID
	private String entityName = "MyVm";// �豸��ʶ
	// �Ƿ���Ҫ����洢����Ĭ��Ϊ��false���رն���洢����ע��ӥ�� Android SDK
	// v3.0���ϰ汾֧����켣�ϴ�ͼ��ȶ������ݣ�����ʹ�ô˹��ܣ��ò�������Ϊ true�����赼��bos-android-sdk-1.0.2.jar��
	private boolean isNeedObjectStorage = false;// ͨ������Ϊfalse��ʹ��
	private LBSTraceClient mLBSTraceClient;// ��ʼ���켣����ͻ���
	private Trace mTrace;// ��ʼ���켣����
	private OnTraceListener mTraceListener;
	private OnTrackListener mTrackListener;
	private HistoryTrackRequest mHistoryTrackRequest;// �����켣ʵ��
	private List<LatLng> trackPoints = new ArrayList<LatLng>();// �켣�㼯��
	private SortType sortType = SortType.asc;// �켣�������
	public static final int PAGE_SIZE = 5000;
	private int pageIndex = 1;

	public BitmapDescriptor bmStart;
	public BitmapDescriptor bmEnd;
	public BitmapDescriptor bmArrowPoint;

	// ·�߸�����
	public Overlay polylineOverlay;
	private Marker mMoveMarker;
	private MapStatus mapStatus;

	// �ؼ���ť���
	private ImageView mSynthesis;
	private ImageView mQueryLine;
	private ImageView mMapModel;
	private int mIndex = 1;// ���ж�ͼƬ

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getActivity().getApplicationContext());
		View view = inflater
				.inflate(R.layout.map_home_layout, container, false);

		mContext = getActivity();// ��ʼ����ǰ������

		initControl(view);// ��ʼ���ؼ�
		initLocation();// ��ʼ����λ
		initMarkers(view);// ��ʼ��������

		setHasOptionsMenu(true);// ��ʾmenu�˵�
		return view;
	}

	/**
	 * �÷������ڳ�ʼ���ؼ�
	 */
	public void initControl(View view) {
		// ��������ؼ�
		mMapView = (MapView) view.findViewById(R.id.map_view_Id);
		mMapView.getChildAt(0).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				/*
				 * ���ؼ�����ȡ�ӿؼ����¼� �����ӿؼ��Լ�����
				 */
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:// ���ƻ���View
					// �����ƻ�������ؼ���ʱ�򽻸����Լ�����������¼�
					mMapView.requestDisallowInterceptTouchEvent(true);
					break;
				case MotionEvent.ACTION_UP:// ����̧��View����DOWN��Ӧ��
				case MotionEvent.ACTION_CANCEL:// ����Ϊԭ����������¼�
					mMapView.requestDisallowInterceptTouchEvent(false);
					break;
				}
				return false;
			}
		});

		mBaiduMap = mMapView.getMap();// �õ���ǰ��ͼ��ʹ��ǰ�Ĳ����������Ѿ��򿪵ĵ�ͼ�ϲ���

		mTrace = new Trace(serviceId, entityName, isNeedObjectStorage);// ��ʼ���켣����
		mLBSTraceClient = new LBSTraceClient(getActivity()
				.getApplicationContext());// ��ʼ���켣����ͻ���
		// �����ʶ
		int tag = 1;
		// ������ʷ�켣����ʵ��
		mHistoryTrackRequest = new HistoryTrackRequest(tag, serviceId,
				entityName);

		mBaiduMap.setOnMarkerClickListener(this);// ���õ���¼�
		mBaiduMap.setOnMapClickListener(this);// ����¼�

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);// ���õ�ͼ�����ű���
		mBaiduMap.setMapStatus(msu);// ����

		/*
		 * ��ʼ����ͼ�ϵİ�ť
		 */
		mLinearLayout = (LinearLayout) view
				.findViewById(R.id.LinearLayout_button_id);
		mLinearLayout.setVisibility(View.VISIBLE);// ��ʾ����

		mTrajectory = (Button) view.findViewById(R.id.trajectory_id);
		if (mTrajectory.isClickable()) {// �Ƿ���
			mTrajectory.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						mTrajectory.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (mTrajectoryOffAndOn) {
									startTrack();// ����ӥ��
									mLBSTraceClient.startTrace(mTrace,
											mTraceListener);// �����켣����
									mLBSTraceClient.startGather(mTraceListener);// �����ɼ�

									mTrajectory
											.setText(getString(R.string.menu_trajectory_on_string));// ����Ϊ�ر�
									mTrajectoryOffAndOn = false;

								} else {
									// ֹͣ����
									mLBSTraceClient.stopTrace(mTrace,
											mTraceListener);
									mLBSTraceClient.stopGather(mTraceListener);// ֹͣ�ɼ�

									// ��������
									mBaiduMap.clear();

									mTrajectory
											.setText(getString(R.string.menu_trajectory_off_string));// ����Ϊ����
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
	 * ��ʼ����λ��Ϣ
	 */
	public void initLocation() {
		mLocationMode = LocationMode.NORMAL;// Ĭ��Ϊ��ͨĬ��

		mLocationClient = new LocationClient(getActivity());

		mLocationListener = new MyLocationListener();// ʵ�������������

		mLocationClient.registerLocationListener(mLocationListener);// ע�������

		// ����һЩ��Ҫ������
		LocationClientOption option = new LocationClientOption();// ��λ����
		option.setCoorType("bd09ll");// ���嵱ǰ��������
		option.setIsNeedAddress(true);// ���ص�ǰλ��
		option.setOpenGps(true);// ��GPS��λ
		option.setScanSpan(5000);// 5000����--5��

		mLocationClient.setLocOption(option);// ��������øò������ܵ����������ò���Ч������

		// ��ʼ��ͼ��
		mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.up);// ѡ��һ��ͼ��R.drawable.up

		mMyOrientationListener = new MyOrientationListener(mContext);// ʵ�������򴫸���
		mMyOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {

					@Override
					public void OrientationChanged(float x) {
						// ���õ�����֮����µ�ͼ�Ϸ���ͼ���λ��
						mCurrenX = x;
					}
				});
	}

	/**
	 * ��ʼ��������
	 */
	private void initMarkers(View view) {
		mMarker = BitmapDescriptorFactory.fromResource(R.drawable.location);
		mMarkerLayout = (RelativeLayout) view.findViewById(R.id.id_marker_ly);// ��ʼ������
	}

	/**
	 * ����ӥ��
	 */
	public void startTrack() {
		// ʵ���������켣����ص��ӿ�
		// ��ʼ���켣���������
		mTraceListener = new OnTraceListener() {

			// ��������ص�
			@Override
			public void onStartTraceCallback(int status, String message) {
				Log.d("TrackRecord", "StartTrace:�ɹ�" + status + "message:"
						+ message);
			}

			// ֹͣ����ص�
			@Override
			public void onStopTraceCallback(int status, String message) {
				Log.d("TrackRecord", "StopTrace:�ɹ�" + status + "message:"
						+ message);
			}

			// �����ɼ��ص�
			@Override
			public void onStartGatherCallback(int status, String message) {
				Log.d("TrackRecord", "StartGather:�ɹ�" + status + "message:"
						+ message);
			}

			// ֹͣ�ɼ��ص�
			@Override
			public void onStopGatherCallback(int status, String message) {
				Log.d("TrackRecord", "StopGather:�ɹ�" + status + "message:"
						+ message);
			}

			// ���ͻص�
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
		int gatherInterval = 5; // λ�òɼ�����
		int packInterval = 15;// �������
		mLBSTraceClient.setInterval(gatherInterval, packInterval);// ����λ�òɼ��ʹ������
		mLBSTraceClient.setProtocolType(ProtocolType.HTTP);// ����Э�����ͣ�0Ϊhttp��1Ϊhttps

		queryTrajectory();
	}

	/**
	 * ӥ�۲�ѯʵʱλ��
	 */
	public void queryTrajectory() {
		// ���ù켣��ѯ��ֹʱ��
		long startTime = System.currentTimeMillis() / 1000 - 12 * 60 * 60;// ��ʼʱ��(��λ����)
		long endTime = System.currentTimeMillis() / 1000;// ����ʱ��(��λ����)

		mHistoryTrackRequest.setStartTime(startTime);// ���ÿ�ʼʱ��
		mHistoryTrackRequest.setEndTime(endTime);// ���ý���ʱ��
		mHistoryTrackRequest.setProcessed(true);// ���ÿ�������ƫ��

		// ������ƫѡ��ʵ��
		ProcessOption processOption = new ProcessOption();// ������Ҫ���õĲ���
		processOption.setNeedDenoise(true);// ������Ҫȥ��
		processOption.setNeedVacuate(true);// ������Ҫ��ϡ
		processOption.setNeedMapMatch(true);// ������Ҫ��·
		processOption.setRadiusThreshold(100);// ���þ��ȹ���ֵ(��λ���ȴ���100�׵Ĺ��˵�)
		processOption.setTransportMode(TransportMode.walking);// ����Ϊ����

		mHistoryTrackRequest.setProcessOption(processOption);// ���þ�ƫѡ��
		// ���������䷽ʽΪ����
		mHistoryTrackRequest.setSupplementMode(SupplementMode.walking);

		// ��ʼ���켣������
		mTrackListener = new OnTrackListener() {

			// ��ʷ�켣�ص�
			@Override
			public void onHistoryTrackCallback(HistoryTrackResponse response) {
				int toal = response.getTotal();
				if (StatusCodes.SUCCESS != response.getStatus()) {
					Toast.makeText(mContext, response.getMessage(),
							Toast.LENGTH_LONG);
				} else if (0 == toal) {
					Toast.makeText(mContext, "δ�鵽�켣", Toast.LENGTH_LONG);
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
				// ��ѯ��ʷ�켣
				queryTrajectory();
			}
		};
		mLBSTraceClient.queryHistoryTrack(mHistoryTrackRequest, mTrackListener);
	}

	/**
	 * ������ʷ�켣
	 */
	public void drawHistoryTrack(final List<LatLng> points, SortType sortType) {
		// �����¸�����ǰ�����֮ǰ�ĸ�����
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

		// ��������յ�ͼ��
		bmStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);// �������ͼ��
		bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.down);// �����յ�ͼ��
		bmArrowPoint = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_end);

		if (points.size() == 1) {// ֻ��һ��������
			OverlayOptions startOptions = new MarkerOptions()
					.position(points.get(0)).icon(bmStart).zIndex(9)
					.draggable(true);
			mBaiduMap.addOverlay(startOptions);

			animateMapStatus(points.get(0), 18.0f);
			return;
		}

		// ����յ㾭γ��
		LatLng startPoint;
		LatLng endPoint;
		if (sortType == SortType.asc) {
			startPoint = points.get(0);
			endPoint = points.get(points.size() - 1);
		} else {
			startPoint = points.get(points.size() - 1);
			endPoint = points.get(0);
		}

		// ������ͼ��
		OverlayOptions startOptions = new MarkerOptions().position(startPoint)
				.icon(bmStart).zIndex(9).draggable(true);
		mBaiduMap.addOverlay(startOptions);// ������

		// ����յ�ͼ��
		OverlayOptions endOptions = new MarkerOptions().position(endPoint)
				.icon(bmEnd).zIndex(9).draggable(true);
		mBaiduMap.addOverlay(endOptions);// ����յ�

		// ���·�ߣ��켣��
		OverlayOptions polylineOptions = new PolylineOptions().width(10)
				.color(Color.BLUE).points(points);// ����ɫ��������ʾ
		polylineOverlay = mBaiduMap.addOverlay(polylineOptions);// ���·��

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
		mBaiduMap.animateMapStatus(msu);// ���µ�ͼ
	}

	@Override
	public void onClick(View v) {
		/*
		 * ���а�ť�ĵ���¼�
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
		case R.id.Synthesis_id:// �ۺϰ�ť
			new BottomDialogUtil(getActivity()) {

				@Override
				public void GeneralMap() {
					/*
					 * ��ͨ��ͼ�߼�
					 */
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);// ���õ�ͼ����Ϊ��ͨ��ͼ
				}

				@Override
				public void SatelliteMap() {
					/*
					 * ���ǵ�ͼ�߼�
					 */
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);// ���õ�ͼ����Ϊ���ǵ�ͼ
				}

				@Override
				public void RealTimeTraffic() {
					/*
					 * ʵʱ��ͨ�߼�
					 */
					if (mBaiduMap.isTrafficEnabled()) {// �����ж��Ƿ��Ѿ���ʾ��ʵʱ��ͨ
						mBaiduMap.setTrafficEnabled(false);// ����Ѿ�����ر���
					} else {
						mBaiduMap.setTrafficEnabled(true);
					}
				}

			}.show();// ��ʾ
			break;
		case R.id.queryLine_id:
			break;
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// ��ͼ�ĵ���¼�
		mMarkerLayout.setVisibility(View.GONE);// ���ز���
		mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Bundle bundleInfo = marker.getExtraInfo();// �õ�������bundle������
		Info info = (Info) bundleInfo.getSerializable("info");// �����ֵȥ����Ӧ������

		/*
		 * mMarkerLayoutͨ���󲼾�ȡ�����еĶ���
		 */
		ImageView imageView = (ImageView) mMarkerLayout
				.findViewById(R.id.id_info_img);
		TextView distence = (TextView) mMarkerLayout
				.findViewById(R.id.id_info_distance);
		TextView name = (TextView) mMarkerLayout
				.findViewById(R.id.id_info_name);
		TextView zan = (TextView) mMarkerLayout.findViewById(R.id.id_info_zan);

		// �������
		imageView.setImageResource(info.getImageId());
		distence.setText(info.getDistance());
		name.setText(info.getName());
		int zans = info.getZan();
		zan.setText(String.valueOf(zans));

		InfoWindow infoWindow;

		TextView tv = new TextView(mContext);// �ڵ�ǰ��ϴ���һ���ı���
		tv.setBackground(getResources().getDrawable(R.drawable.img_bottom));
		tv.setPadding(30, 20, 30, 50);
		tv.setText(info.getName());

		final LatLng latLng = marker.getPosition();// ��ȡ��γ��
		Point point = mBaiduMap.getProjection().toScreenLocation(latLng);// �Ѿ�γ��ת������ͨ�ĵ�
		point.y -= 47;

		LatLng ll = mBaiduMap.getProjection().fromScreenLocation(point);

		infoWindow = new InfoWindow(tv, ll, 0);

		mBaiduMap.showInfoWindow(infoWindow);// ��ʾ�������

		mMarkerLayout.setVisibility(View.VISIBLE);// ��ʾ����
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
	// * �˵��İ�ť�ĵ������
	// */
	// switch (item.getItemId()) {// ���ݵ���Ĳ˵�id�������ж�
	// case R.id.Menu_GeneralMapId:// ��ͨ��ͼ
	// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);// ���õ�ͼ����Ϊ��ͨ��ͼ
	// break;
	// case R.id.Menu_SatelliteMapId:// ���ǵ�ͼ
	// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);// ���õ�ͼ����Ϊ���ǵ�ͼ
	// break;
	// case R.id.Menu_RealTime_trafficId:// ���õ�ͼ����Ϊʵʱ��ͨ��ͼ
	// if (mBaiduMap.isTrafficEnabled()) {// �����ж��Ƿ��Ѿ���ʾ��ʵʱ��ͨ
	// mBaiduMap.setTrafficEnabled(false);// ����Ѿ�����ر���
	// item.setTitle(getString(R.string.menu_real_time_traffic_off_string));
	// } else {
	// mBaiduMap.setTrafficEnabled(true);
	// item.setTitle(getString(R.string.menu_real_time_traffic_on_string));
	// }
	// break;
	// case R.id.Menu_My_AddressId:// �ҵ�λ��
	// centerToMyAddress();
	// break;
	// case R.id.Menu_normalModeId:// ��ͨģʽ
	// mLocationMode = LocationMode.NORMAL;
	// break;
	// case R.id.Menu_followModeId:// ����ģʽ
	// mLocationMode = LocationMode.FOLLOWING;
	// break;
	// case R.id.Menu_compassModeId:// ����ģʽ
	// mLocationMode = LocationMode.COMPASS;
	// break;
	// case R.id.Menu_addObstacleId:// ��Ӹ�����
	// addObstacle(Info.infos);
	// break;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	/**
	 * ��Ӹ�����
	 * 
	 * @param infos
	 */
	private void addObstacle(List<Info> infos) {
		mBaiduMap.clear();// ���һЩ����Ҫ��ͼ��

		LatLng latLng = null;// ���ڽ��վ�γ��

		Marker marker = null;
		OverlayOptions options;

		for (Info bl : infos) {
			// Ϊ��γ�ȸ�ֵ
			latLng = new LatLng(bl.getLatitube(), bl.getLongitube());
			// ͼ��
			options = new MarkerOptions().position(latLng)// �����ڵ�ͼ�ϵ�ʲôλ��
					.icon(mMarker)// ͼ��
					.zIndex(5);// 5Ϊ��ͼ���ϲ���ʾ

			marker = (Marker) mBaiduMap.addOverlay(options);// ���÷�����Ӹ�����

			Bundle bundle = new Bundle();// bundle�����������ݴ���
			bundle.putSerializable("info", bl);

			marker.setExtraInfo(bundle);
		}

		// �ƶ���ͼ����ǰλ��
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);// ���뵱ǰλ��
		mBaiduMap.setMapStatus(msu);// ����λ��
	}

	/**
	 * ��λ���ҵ�λ��
	 */
	public void centerToMyAddress() {
		LatLng latLng = new LatLng(mLatitube, mLongitube);// ���뵱ǰ��γ��

		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);// ���뵱ǰλ��
		mBaiduMap.animateMapStatus(msu);// ��λ����ǰ��λ��
	}

	/**
	 * ��λ
	 * 
	 * @author ��ͿͿi
	 * 
	 */
	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			/*
			 * ����λ�ɹ��Ժ���д˷����Ļص�
			 */
			MyLocationData data = new MyLocationData.Builder()
					.direction(mCurrenX)// ����λ�ɹ�����÷��򴫸���֮����з��������
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())// γ��
					.longitude(location.getLongitude())// ����
					.build();

			mBaiduMap.setMyLocationData(data);// ������ת������ͼ����

			/*
			 * ����һ���Զ���ͼ��,LocationMode.NORMAL����ģʽ
			 * LocationMode.NORMALģʽ��mIconLocationͼ�����
			 */
			MyLocationConfiguration config = new MyLocationConfiguration(
					mLocationMode, true, mIconLocation);
			mBaiduMap.setMyLocationConfiguration(config);// �����ü��ؽ���

			// ÿ�ζ�λ�ɹ��󽫸��´�ž�γ�ȵı���
			mLatitube = location.getLatitude();// �����»�ȡ�����ݴ������
			mLongitube = location.getLongitude();// �����»�ȡ�����ݴ������

			if (mInFirstIn) {// �Ƿ�Ϊ��һ��
				/*
				 * ֻ�е�һ�ν�����ʱ��ᶨλ���û������ĵ�,Ҳ��������λ��
				 */
				LatLng latLng = new LatLng(location.getLatitude(),
						location.getLongitude());// ���Ⱥ�γ�ȵķ���
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);// ���뵱ǰλ��

				mBaiduMap.animateMapStatus(msu);

				mInFirstIn = false;// ��һ��֮������Ϊfalse

				// Toast.makeText(mContext, "" + location.getAddrStr(),
				// Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
		// onResumeִ��֮��ִ���������,�ڴ˷����п�����λ
		mBaiduMap.setMyLocationEnabled(true);// ��ͼ������λ������

		if (!mLocationClient.isStarted()) {// ���û��������������λ
			mLocationClient.start();

			// �������򴫸���
			mMyOrientationListener.start();// �������������һ��
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mBaiduMap.setMyLocationEnabled(false);// �ر�Ӧ��֮���ͼ����������λ
		mLocationClient.stop();// ֹͣ��λ
		mMyOrientationListener.stop();// �رշ��򴫸���

		// ӥ�� ��һ���Ժ��´�ʹ�ÿ��Բ���������
		mLBSTraceClient.stopTrace(mTrace, mTraceListener);// ֹͣ����
		mLBSTraceClient.stopGather(mTraceListener);// ֹͣ�ɼ�
		mLBSTraceClient.clear();// ������Դ
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
	}
}
