package github.racolin.busmap;

import android.content.Context;
import android.icu.text.SimpleDateFormat;

import java.text.Normalizer;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import github.racolin.busmap.component.BusDistance;
import github.racolin.busmap.component.BusStopRaw;
import github.racolin.busmap.component.Result;
import github.racolin.busmap.component.ResultRoute;
import github.racolin.busmap.config.LevelExtend;
import github.racolin.busmap.config.MoveType;
import github.racolin.busmap.config.Speed;
import github.racolin.busmap.data.BusStopDAO;
import github.racolin.busmap.data.RouteDAO;
import github.racolin.busmap.data.StationDAO;
import github.racolin.busmap.entities.Address;
import github.racolin.busmap.entities.Station;

public class Support {
//    hàm chuyển chuỗi từ nhiều ký tự sang dạng ...
//    Ví dụ từ "Đại học Sư phạm Kỹ thuật Thành phố Hồ Chí Minh"
//    => Đại học Sư phạm Kỹ thuật...
    public static String toStringEllipsis(String str, int max) {
        if (str.length() <= max) {
            return str;
        } else {
            return str.substring(0, max - 3) + "...";
        }
    }
//    Hàm chuyển từ kiểu dữ liệu date sang string với format được nhập vào
    public static String dateToString(Date date, String fm) {
        SimpleDateFormat format = new SimpleDateFormat(fm);
        return format.format(date);
    }

//    Hàm chuyển từ kiểu dữ liệu string sang util.date với format được nhập vào
    public static Date stringToDate(String date, String fm) {
        SimpleDateFormat format = new SimpleDateFormat(fm);
        Date dt = new Date();
        try {
            dt = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

//    Hàm chuyển tiếng việt có dấu sang tiếng việt không dấu
    public static String covertToString(String value) {
        try {
            String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

//    Hàm chuyển từ kiểu dữ liệu string sang localtime với format được nhập vào
    public static LocalTime stringToLocalTime(String time, String fm) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern(fm));
    }

//    Hàm chuyển từ kiểu dữ liệu localtime sang string với format được nhập vào
    public static String timeToString(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

//    Hàm chuyển đổi thành tiền (VND)
    public static String toCurrency(int price) {
        String currency = "";
        int count = 0;
        int m = price;
        while (m != 0) {
            currency = m % 10 + currency;
            m /= 10;
            if (++count % 3 == 0 && m != 0) {
                currency = "," + currency;
            }
        }
        return currency + " VND";
    }
//    Hàm lấy một iterator level quét các điểm xung quanh của vị trí đang xét
//    Được dùng khi cải tiến thuật toán lên
//    Hiện tại chưa sử dụng
    private static Iterator<Integer> getLevelList() {
        List<Integer> level = new ArrayList<>();
        level.add(LevelExtend.MIN);
        level.add(LevelExtend.LOW);
        level.add(LevelExtend.NORMAL);
        level.add(LevelExtend.HIGH);
        level.add(LevelExtend.MAX);
        return level.iterator();
    }

//    Hàm check email có hợp lệ hay không bằng regex
//    Email hợp lệ là email bắt đầu bằng chữ cái,
//    tiếp theo có thể là số hoặc chữ và cuối cùng là @gmail.com
//    Có thể nâng cấp regex
    public static boolean checkInvalidEmail(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+(\\w*[a-zA-Z]*)*@gmail.com$");
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }

//    Hàm check password hợp lệ
//    Regex ở dưới sẽ match với chuỗi có độ dài từ 8 đến 20
//    Và phải chứa các số, chữ thường, chữ hoa
    public static boolean checkInvalidPassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

//    Hàm tính khoảng cách giữa 2 điểm bất kỳ
    public static int calculateDistance(Address a, Address b) {
        if (a == null || b == null) {
            return -1;
        }
        double x = Math.abs(a.getLat() - b.getLat()) * 110000;
        double y = Math.abs(a.getLng() - b.getLng()) * 110000;
        return (int) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

//    Hàm chuyển đổi khoảng cách thành thời gian
//    Với đầu vào là khoảng cách và cách di chuyển
//    Cách di chuyển bằng bus, hay walk được định nghĩa trong package config trong lớp Speed
//    Thời gian bằng quãng đường chia vận tốc
    public static String distanceToTime(int meter, MoveType type) {
        if (meter == -1) {
            return "";
        }
        int speed = 0;
        if (type == MoveType.BUS) {
            speed = Speed.BUS;
        } else {
            speed = Speed.WALK;
        }
        int result = meter / speed;
        if (meter / speed == 0) {
            result = 1;
        }
        return result + " phút";
    }

//    Hàm chuyển từ mét là integer sang km là string
    public static String toKilometerString(int meter) {
        double k = meter / 1000.0;
        return k + "km";
    }

//    Hàm chuyển từ mét là integer sang mét là string
    public static String toMeterString(int meter) {
        return meter + " mét";
    }

//    Hàm tính giời gian di chuyển trong một khoảng meter với vận tốc là speed đầu vào
    public static int calculateTimePass(int meter, int speed) {
        return meter / speed;
    }

//    Chuyển đổi thì mét sang độ
    public static double convertMeterToDegree(int meter) {
        return meter / 111000.0;
    }

//    Chuyển đổi từ độ sang mét
    public static int convertDegreeToMeter(double degree) {
        return (int) degree * 111000;
    }

//    Lý do của việc đem thuật toán ra đây là để khi nào cập nhật thuật toán thì chỉ cần mở file này lên edit

//  Hàm này tính kết quả từ điểm đến, điểm đi và số lượng tuyến đi qua
//  Đầu tiên lấy các station xung quanh của cả 2 điểm, sau đó tính ra các tuyến đi qua gần 2 điểm đó
//  Cuối cùng thì chr cần tìm tuyến nàm trong 2 list

    public static List<Result> calculateResults(Context context, Address from, Address to, int route_amount) {
//        Lấy một list các stations xung quanh điểm bắt đầu
        List<Station> starts = StationDAO.getStationsAround(context,
                from.getLat(), from.getLng(), convertMeterToDegree(LevelExtend.HIGH));
//        Tương tự, lấy một list các stations xung quanh điểm đến
        List<Station> ends = StationDAO.getStationsAround(context,
                to.getLat(), to.getLng(), convertMeterToDegree(LevelExtend.HIGH));

//        Gọi hàm calculate phía dưới để tính ra 2 dictionary chứa các route đi qua gần 2 điểm này
        Dictionary<String, BusDistance> dictionary_starts = calculate(context, starts, from);
        Dictionary<String, BusDistance> dictionary_ends = calculate(context, ends, to);

        List<Result> results = new ArrayList<>();
        results = findResults(context, dictionary_starts, dictionary_ends);
        if (route_amount > 0) {

        }
        if (route_amount > 1) {

        }
        return results;
    }

    public static List<Result> findResults(Context context, Dictionary<String, BusDistance> dictionary_starts,
                                           Dictionary<String, BusDistance> dictionary_ends) {

//        Cuối cùng tìm điểm
        Enumeration<String> enumeration = dictionary_starts.keys();
        List<Result> results = new ArrayList<>();

        while (enumeration.hasMoreElements()) {
            String current_route = enumeration.nextElement();
            if (dictionary_ends.get(current_route) != null
                    && dictionary_starts.get(current_route).getBus_stop_raw().getOrder() < dictionary_ends.get(current_route).getBus_stop_raw().getOrder()) {
                List<ResultRoute> resultRoutes = new ArrayList<>();

                resultRoutes.add(new ResultRoute(RouteDAO.getRouteByID(context, current_route),
                        BusStopDAO.convertRawToBusStop(context, dictionary_starts.get(current_route).getBus_stop_raw()),
                        BusStopDAO.convertRawToBusStop(context, dictionary_ends.get(current_route).getBus_stop_raw())));

                results.add(new Result(resultRoutes , dictionary_starts.get(current_route).getDistance(),
                        dictionary_ends.get(current_route).getDistance()));
            }
        }
        return results;
    }

//    Hàm nhận đầu vào là một list các stations và một địa điểm
   /* Hàm duyệt qua tất cả các stations và lần lượt lấy được tất cá các bus_stop_raw
     Sau đó ta kiểm tra trong dictionary kết quả xem có đã có chứa route mà bus_stop_raw đang chứa hay không
     Nếu chưa thì add một item của dictionary
     Nếu rồi thì ta sẽ kiểm tra xem khoảng cách của station đó có bé hơn khoảng cách của station cũ hay không để cập nhật */
   /* bus_stop_raw cũng giống như là bus_stop nhưng bus_stop_raw
    chỉ chứa các giá trị như trong database chứ không load thêm data.
    Chẳng hạn BusStop sẽ chứa các Station nhưng BusStopRaw chỉ chứa station_id...
    */
    public static Dictionary<String, BusDistance> calculate(Context context, List<Station> stations, Address address) {
//        dictionary có key là route_id và values là BusDistance
//        BusDistance chứa bus_stop_raw và khoảng cách từ điểm xét đến station
        Dictionary<String, BusDistance> dictionary = new Hashtable<>();

        BusDistance busDistance;
//        Duyệt toàn bộ stations
        for (Station station : stations) {
//            Tính ra distance và lấy được một list các bus_stop_raw
            int distance = calculateDistance(address, station.getAddress());
            List<BusStopRaw> raws = BusStopDAO.getBusStopRawFromStationId(context, station.getId());
//            Duyệt qua hết bus stop raw
            for (BusStopRaw raw : raws) {
//                Check xem route_id của bus stop raw đã tồn tại trong dictionary chưa
                busDistance = dictionary.get(raw.getRoute_id());
                if (busDistance == null) {
//                Nếu chưa thì thêm vào dictionary
                    dictionary.put(raw.getRoute_id(), new BusDistance(raw, distance));
                } else {
//                    Nếu đã có rồi thì so sánh khoảng cách của bustop mới có gần hơn không
//                    Nếu gần hơn thì cập nhật value
                    if (busDistance.getDistance() > distance) {
                        dictionary.put(raw.getRoute_id(), new BusDistance(raw, distance));
                    }
                }
            }
        }

        return dictionary;
    }
}
