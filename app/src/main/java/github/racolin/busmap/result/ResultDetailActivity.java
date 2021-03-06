package github.racolin.busmap.result;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import github.racolin.busmap.R;
import github.racolin.busmap.Support;
import github.racolin.busmap.component.BusStopGuide;
import github.racolin.busmap.component.Result;
import github.racolin.busmap.component.ResultRoute;
import github.racolin.busmap.component.RouteGuide;
import github.racolin.busmap.config.MoveType;
import github.racolin.busmap.data.BusStopDAO;
import github.racolin.busmap.entities.Address;
import github.racolin.busmap.entities.BusStop;
import github.racolin.busmap.entities.Station;
import github.racolin.busmap.listener.OnBusStopListener;
import github.racolin.busmap.listener.OnRouteListener;
import github.racolin.busmap.route.RouteIconAdapter;

public class ResultDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback, OnRouteListener, OnBusStopListener {
//    C??c view ????? ??nh x???
    ViewPager2 vp2_detail_route;
    RecyclerView rv_routes_icon;
    List<BusStopGuide> busStopGuides;
    List<RouteGuide> routeGuides;
    Result result;
    Address from, to;
    int totalDistance;
//  c??c object c???n thi???t cho map
    GoogleMap map;
    List<Marker> markers;
    Bitmap ic_big, ic_focus, ic_walk, ic_walk_focus, ic_dest_focus, ic_dest;
    List<Polyline> polylines;
    int pre_marker = 0, pre_polyline = 0;
//    ll x??? d???ng cho vi???c l?????t ph???n chi ti???t route l??n v?? xu???ng
    LinearLayout ll;
//    height l?? chi???u cao c???a screen, down l?? v??? tr?? t????ng ?????i c???a y khi v???a drag v??o linear layout drag
    int height, down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(R.string.moving_guide);

        getData();

        initMap();
        initUI();
        initListener();
    }

// get result route s??? l???y data t??? activity tr?????c truy???n t???i g???m result v?? ??i???m ??i v??  ??i???m ?????n
    private void getData() {
        Intent intent = getIntent();
        result = (Result) intent.getSerializableExtra("result");
        from = (Address) intent.getSerializableExtra("from");
        to = (Address) intent.getSerializableExtra("to");
    }

    private void initMap() {

        Drawable ic = getDrawable(R.drawable.ic_station_big);
        int width = ic.getIntrinsicWidth();
        int height = ic.getIntrinsicHeight();
        ic_big = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_big));

        ic = getDrawable(R.drawable.ic_walk_map);
        width = ic.getIntrinsicWidth();
        height = ic.getIntrinsicHeight();
        ic_walk = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_walk));

        ic = getDrawable(R.drawable.ic_walk_map_focus);
        width = ic.getIntrinsicWidth();
        height = ic.getIntrinsicHeight();
        ic_walk_focus = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_walk_focus));

        ic = getDrawable(R.drawable.ic_destination);
        width = ic.getIntrinsicWidth();
        height = ic.getIntrinsicHeight();
        ic_dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_dest));

        ic = getDrawable(R.drawable.ic_destination_focus);
        width = ic.getIntrinsicWidth();
        height = ic.getIntrinsicHeight();
        ic_dest_focus = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_dest_focus));

        ic = getDrawable(R.drawable.ic_station_focus);
        width = ic.getIntrinsicWidth();
        height = ic.getIntrinsicHeight();
        ic_focus = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ic.setBounds(0, 0, width, height);
        ic.draw(new Canvas(ic_focus));
        setGuides();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fm_map);
        mapFragment.getMapAsync(this);
    }

    private void initUI() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        height = displayMetrics.heightPixels;
        ll = findViewById(R.id.ll);
        rv_routes_icon = findViewById(R.id.rv_routes_icon);
        rv_routes_icon.setNestedScrollingEnabled(false);
        RouteIconAdapter adapter = new RouteIconAdapter(this, result.getResult_routes());
        rv_routes_icon.setAdapter(adapter);
        rv_routes_icon.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        vp2_detail_route = findViewById(R.id.vp2_detail_route);
        ResultStateAdapter stateAdapter = new ResultStateAdapter(this, routeGuides, busStopGuides, this, this);
        vp2_detail_route.setAdapter(stateAdapter);

        new TabLayoutMediator(findViewById(R.id.tab_layout), vp2_detail_route, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.guide_detail);
                        break;
                    case 1:
                        tab.setText(R.string.pass_bus_stop);
                        break;
                }
            }
        }).attach();
        middleRouteDetail();
    }

    private void initListener() {
        rv_routes_icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {int y = (int) motionEvent.getRawY();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        down = (int) motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        setHeightRouteDetail(height - y + down);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (y - rv_routes_icon.getHeight() / 2 > height * 3/ 4) {
                            collapseRouteDetail();
                        } else {
                            if (y - rv_routes_icon.getHeight() / 2 < height / 4) {
                                expandRouteDetail();
                            }
                            else {
                                middleRouteDetail();
                            }
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    private void setHeightRouteDetail(int height) {
        height = Math.max(height, rv_routes_icon.getHeight());
        height = Math.min(height, this.height - getSupportActionBar().getHeight());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ll.setLayoutParams(new RelativeLayout.LayoutParams(layoutParams));
    }

    private void expandRouteDetail() {
        setHeightRouteDetail(height - getSupportActionBar().getHeight());
    }

    private void collapseRouteDetail() {
        setHeightRouteDetail(rv_routes_icon.getHeight());
    }

    private void middleRouteDetail() {
        setHeightRouteDetail(height / 2);

    }

//    t??? result t??nh to??n ???????c c??c routes guide cho ng?????i d??ng
//    x??c ?????nh l?? t??? ??i???m ??i ?????n tr???m ?????u ti??u l?? m???t route
//    t??? tr???m xu???ng ?????n ??i???m k???t th??c c??ng l?? m???t route
//    ??? gi???a l??? tr??nh ??i c?? th??? ng???i m???t ho???c nhi???u tuy???n xe n???a
    private void setGuides() {
        routeGuides = new ArrayList<>();
        busStopGuides = new ArrayList<>();
//        Route t??? ??i???m b???t ?????u ?????n tr???m g???n nh???t
        routeGuides.add(new RouteGuide("??i ?????n " + result.getResult_routes().get(0).getBusStop_start().getStation().getName(),
                "Xu???t ph??t t??? " + from.getAddress(),
                MoveType.WALK, result.getWalk_distance_start(), ""));

        busStopGuides.add(new BusStopGuide("", from.getAddress(), MoveType.WALK, from));

        Station preStation = null;

//        L???p c??c result route ????? th??m c??c h?????ng d???n v??? c??c tuy???n xe s??? l??n v?? xu???ng
        for (ResultRoute resultRoute : result.getResult_routes()) {
            if (preStation != null) {
                routeGuides.add(new RouteGuide( preStation.getName() + " - " + resultRoute.getBusStop_start().getStation().getName(),
                        "??i xu???ng " + preStation.getName() + " - " + " ??i l??n " + resultRoute.getBusStop_start().getStation().getName(),
                        MoveType.WALK, Support.calculateDistance(preStation.getAddress(), resultRoute.getBusStop_start().getStation().getAddress()), ""));

                busStopGuides.add(new BusStopGuide("", preStation.getName(), MoveType.WALK, preStation.getAddress()));
            }

            int distance = setPartOfBusStopGuides(resultRoute);

            preStation = resultRoute.getBusStop_end().getStation();

            routeGuides.add(new RouteGuide("??i tuy???n " + resultRoute.getRoute().getId() + ": " +
                    resultRoute.getRoute().getName(), resultRoute.getBusStop_start().getStation().getName() + " - " +
                    resultRoute.getBusStop_end().getStation().getName(), MoveType.BUS, distance,
                    resultRoute.getRoute().getMoney()));
        }

        busStopGuides.add(new BusStopGuide("", to.getAddress(), MoveType.WALK, to));

//        Route t??? tr???m d???ng ch??n ?????n ??i???m ?????n
        routeGuides.add(new RouteGuide("??i xu???ng " + result.getResult_routes().get(result.getResult_routes().size() - 1).getBusStop_end().getStation().getName(),
                "??i ?????n " + to.getAddress(), MoveType.WALK,
                result.getWalk_distance_end(), ""));
    }

//    t????ng t??? routes guide th?? bus stop guide c??ng t????ng t??? nh?? v???y
//    ??i???m b???t ?????u c??ng ???????c coi nh?? m???t tr???m cho n??n di chuy???n t??? ??i???m b???t ?????u
//    ?????n tr???m g???n nh???t c??ng coi nh?? l?? m???t h??nh tr??nh t??? tr???m n??y qua tr???m kh??c

//    tr???m xu???ng ?????n ??i???m ?????n c??ng s??? ???????c coi nh?? h??nh tr??nh t??? tr???m n??y qua tr???m kh??c
    private int setPartOfBusStopGuides(ResultRoute resultRoute) {
        int distance = 0;
        int s = resultRoute.getBusStop_start().getOrder(), e = resultRoute.getBusStop_end().getOrder();
        for (BusStop busStop : BusStopDAO.getBusStopsFromRouteIdAndOrder(this, resultRoute.getRoute().getId(), s, e)) {
            distance += busStop.getDistance_previous();
            busStopGuides.add(new BusStopGuide(busStop.getRoute_id(), busStop.getStation().getName(),
                    MoveType.BUS, busStop.getStation().getAddress()));

        }

        return distance;
    }

    @Override
    public void setOnBusStopClickListener(int position) {
        int len = markers.size();
        if (position == 0) {
            markers.get(position).setIcon(BitmapDescriptorFactory.fromBitmap(ic_walk_focus));
        }
        if (position == len - 1) {
            markers.get(position).setIcon(BitmapDescriptorFactory.fromBitmap(ic_dest_focus));
        }
        if (position > 0 && position < len - 1) {
            markers.get(position).setIcon(BitmapDescriptorFactory.fromBitmap(ic_focus));
        }
        if (pre_marker == 0) {
            markers.get(pre_marker).setIcon(BitmapDescriptorFactory.fromBitmap(ic_walk));
        }
        if (pre_marker == len - 1) {
            markers.get(pre_marker).setIcon(BitmapDescriptorFactory.fromBitmap(ic_dest));
        }
        if (pre_marker > 0 && pre_marker < len - 1) {
            markers.get(pre_marker).setIcon(BitmapDescriptorFactory.fromBitmap(ic_big));
        }
        pre_marker = position;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(markers.get(position).getPosition(), 15));
    }

    @Override
    public void setOnRouteClickListener(int position) {
        if (pre_polyline != position) {
            if (position == 0 || position == polylines.size() - 1) {
                polylines.get(pre_polyline).setColor(getColor(R.color.primary_600));
            } else {
                polylines.get(pre_polyline).setColor(getColor(R.color.orange));
            }
            polylines.get(position).setColor(getColor(R.color.red));
        }
        pre_polyline = position;
    }

//    T??? c??c routes guide v?? bus stops guide c?? ???????c ta s??? v??? c??c markers v?? polylines
//    ????? bi???u th??? ???????ng ??i
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        markers = new ArrayList<>();
        int len = busStopGuides.size();
        polylines = new ArrayList<>();
//        l1, l2 l???n l?????t l?? 2 ??i???m ?????u c???a h??nh tr??nh
//        l1 l?? ??i???m b???t ?????u
        LatLng l1 = new LatLng(busStopGuides.get(0).getAddress().getLat(),
                busStopGuides.get(0).getAddress().getLng());
//        l2 l?? tr???m ?????u ti??n
        LatLng l2 = new LatLng(busStopGuides.get(1).getAddress().getLat(),
                busStopGuides.get(1).getAddress().getLng());

//        v??? polyline t??? ??i???m b???t ?????u ?????n tr???m ?????u ti??n t???c l?? t??? l1 ?????n l2
        polylines.add(map.addPolyline(new PolylineOptions()
                .add(l1, l2).clickable(false).color(getColor(R.color.red))
                .pattern(Arrays.asList(new Dash(30), new Gap(20)))));
//      v??? marker cho ??i???m b???t ?????u
        markers.add(map.addMarker(new MarkerOptions().position(l1)
                        .zIndex(1f)
                .icon(BitmapDescriptorFactory.fromBitmap(ic_walk_focus))));

//      duy???t t???t c??? c??c busStopGuide ????? ????nh marker cho to??n b??? c??c tr???m

//        C??n ????y l?? ph???n quan tr???ng nh???t. V??? c??c route th?? n???u m?? ch??ng ta c?? nhi???u route th??
//        c???n ph???i v??? c??c polyline cho c??c route ????. N???u 2 route g???i ?????u nhau t???c l??
//        bus stop k???t th??c c???a route n??y ?????ng th???i l?? bus stop b???t ?????u c???a route kia th?? c?? th??? v??? 1 polyline
//        Nh??ng n???u bus stop k???t th??c c???a route tr?????c kh??ng ph???i l?? bus stop b???t ?????u c???a route kia
//        nh?? v???y th?? ta ph???i v??? line cho ??o???n ???????ng ??i gi???a 2 bus stop ????

        String route_id = busStopGuides.get(1).getRoute_id();
        List<LatLng> latLngs = new ArrayList<>();
        for (BusStopGuide busStopGuide : busStopGuides.subList(1, len - 1)) {
            if (!route_id.equals(busStopGuide.getRoute_id())) {
                LatLng[] lls = new LatLng[latLngs.size()];
                latLngs.toArray(lls);

//                V??? line cho route ph??a tr?????c
                polylines.add(map.addPolyline(new PolylineOptions()
                        .add(lls).clickable(false).color(getColor(R.color.orange))));

//                v??? line cho 2 bus stop ????, n???u 2 bus stop ???? l?? m???t th?? lien s??? kh??ng ???????c nh??n th???y
                map.addPolyline(new PolylineOptions()
                        .add(latLngs.get(latLngs.size() - 1),
                                new LatLng(busStopGuide.getAddress().getLat(),
                                        busStopGuide.getAddress().getLng()))
                        .clickable(false).color(getColor(R.color.primary_600))
                        .pattern(Arrays.asList(new Dash(30), new Gap(20))));
                latLngs = new ArrayList<>();
                route_id = busStopGuide.getRoute_id();
            }

//            Chuy???n t???a ????? c???a bus stop v?? latLngs
            latLngs.add(new LatLng(busStopGuide.getAddress().getLat(),
                    busStopGuide.getAddress().getLng()));
//              T??? latLngs c?? th??? ????nh marker cho c??c bus stop
            markers.add(map.addMarker(new MarkerOptions().position(
                            latLngs.get(latLngs.size() - 1))
                    .icon(BitmapDescriptorFactory.fromBitmap(ic_big))));
        }
        LatLng[] lls = new LatLng[latLngs.size()];
        latLngs.toArray(lls);
//        V??? line cho route cu???i c??ng
        polylines.add(map.addPolyline(new PolylineOptions()
                .add(lls).clickable(false).color(getColor(R.color.orange))));

//        l1, l2 l???n l?????t l?? 2 ??i???m cu???i c??ng c???a h??nh tr??nh
//        l1 l?? tr???m cu???i c??ng
        l1 = new LatLng(busStopGuides.get(len - 2).getAddress().getLat(),
                busStopGuides.get(len - 2).getAddress().getLng());
//        l2 l?? ????ch ?????n
        l2 = new LatLng(busStopGuides.get(len - 1).getAddress().getLat(),
                busStopGuides.get(len - 1).getAddress().getLng());

//        V??? marker l?? ??i???m ?????n v???i icon l?? ic_dest

        markers.add(map.addMarker(new MarkerOptions().position(l2)
                        .zIndex(1f)
                .icon(BitmapDescriptorFactory.fromBitmap(ic_dest))));

//        v??? polyline t??? tr???m cu???i c??ng ?????n ??i???m ?????n l?? t??? l1 ?????n l2
        polylines.add(map.addPolyline(new PolylineOptions()
                .add(l1, l2).clickable(false).color(getColor(R.color.primary_600))
                .pattern(Arrays.asList(new Dash(30), new Gap(20)))));

//        Di chuy???n camera ?????n ??i???m b???t ?????u
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(busStopGuides.get(0).getAddress().getLat(),
                        busStopGuides.get(0).getAddress().getLng()), 15));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}