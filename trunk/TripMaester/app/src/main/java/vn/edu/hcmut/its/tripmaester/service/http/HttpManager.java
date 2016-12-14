package vn.edu.hcmut.its.tripmaester.service.http;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import group.traffic.nhn.map.MapFragment;
import group.traffic.nhn.message.MessageItem;
import group.traffic.nhn.trip.PointItem;
import group.traffic.nhn.user.FriendItem;
import group.traffic.nhn.user.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import vn.edu.hcmut.its.tripmaester.R;
import vn.edu.hcmut.its.tripmaester.controller.ICallback;
import vn.edu.hcmut.its.tripmaester.controller.manager.LoginManager;
import vn.edu.hcmut.its.tripmaester.helper.ApiCall;
import vn.edu.hcmut.its.tripmaester.model.Trip;

import static group.traffic.nhn.map.MapFragment.mMapView;
import static vn.edu.hcmut.its.tripmaester.helper.CameraHelper.getMimeType;

// TODO: 12/18/15 THUANLE: TO BE REMOVED
@Deprecated
public class HttpManager {
    static final String URL_GET_LIKE_INFO_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/like/GetLikeInfoOnTrip";
    static final String URL_CREATE_POINT_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/tripdetails/CreatePointOnTrip";
    static final String URL_CREATE_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/trip/CreateTrip";// tokenID/[list
    static final String URL_GET_COMMENTS_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/comment/GetListCommentOnTrip";
    static final String URL_GET_FRIENDS = HttpConstants.HOST_NAME + "/ITS/rest/friend/GetListFriend";
    static final String URL_GET_LIST_POINT_ON_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/tripdetails/GetListPointOnTrip";
    static final String URL_GET_LIST_SHARE_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/trip/GetListShareTrip";
    static final String URL_GET_LIST_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/trip/GetListTrip";
    static final String URL_GET_LIST_PRIVATE_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/trip/GetListPrivateTrip";
    static final String URL_GET_LIST_TRP_SEARCH = HttpConstants.HOST_NAME + "/ITS/rest/trip/GetListTripSearch";
    static final String URL_GET_LOGIN = HttpConstants.HOST_NAME + "/ITS/rest/user/GETlogin/";
    static final String URL_GET_SHARE_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/share/GetShareOnTrip";
    static final String URL_GET_TRIP_INFO = HttpConstants.HOST_NAME + "/ITS/rest/trip/GetTripInfo";
    static final String URL_GET_USER_INFO = HttpConstants.HOST_NAME + "/ITS/rest/user/GetUserInfo";
    static final String URL_LIKE_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/like/LikeTrip";
    static final String URL_LOGIN = HttpConstants.HOST_NAME + "/ITS/rest/user/Login";
    static final String URL_LOGOUT = HttpConstants.HOST_NAME + "/ITS/rest/user/Logout";
    static final String URL_SAVE_FRIENDS = HttpConstants.HOST_NAME + "/ITS/rest/friend/SaveListFriend";
    static final String URL_SAVE_SHARE_TRIP = HttpConstants.HOST_NAME + "/ITS/rest/share/SaveShareOnTrip";
    private static final String TAG = HttpManager.class.getName();
    private static OkHttpClient client = new OkHttpClient();

    //TripComment trip
    /*
        + url: /ITS/rest/comment/SaveTripComment
		+ IRequest data: { tokenId: "...", tripId: "...", content:"..."}
		+ Response data: {code:“...”, description:“...”}
		moved to request
	 */
    @Deprecated
    public static void commentTrip(String tripID, String content, Context context, final ICallback<JSONObject> callback) {
        Ion.with(context).load(CommentTripController.URL_SAVE_COMMENT_TRIP)
                .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                .setBodyParameter("content", content)
                .setBodyParameter("tripId", tripID)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String str_response) {
                        if (e == null) {
                            try {
                                callback.onCompleted(new JSONObject(str_response), null, null);
                            } catch (JSONException e1) {
                                callback.onCompleted(null, null, e1);
                            }
                        } else {
                            callback.onCompleted(null, null, e);
                        }
                    }
                });
    }

    //count like trip
    /*
        + url:
		+ IRequest data: { tokenId: "...", tripId:"..."}
		+ Response data:
	 */
    public static void getLikeInfoOnTrip(String tripID, Context context, final ICallback<JSONObject> callback) {
        Ion.with(context).load(URL_GET_LIKE_INFO_TRIP)
                .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                .setBodyParameter("tripId", tripID)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String str_response) {
                        if (e == null) {
                            try {
                                callback.onCompleted(new JSONObject(str_response), null, null);

                            } catch (Exception ex) {
                                callback.onCompleted(null, null, ex);
                            }
                        } else {
                            callback.onCompleted(null, null, e);
                        }
                    }
                });

    }

    //create point on trip
    /*
     * + url:
	   + IRequest data: { tokenId: "ed16a4cd-1627-43c1-a6b3-9b3b925742ea", tripId: "317e5cc8-b2e4-48e5-93e3-8efaa4a02cfe", x:"a0"
		, y: "a0", z:"a0", fromZ:"a0", pointDescription:"a0",order: "a0"}
	   + Response data:
	 */
    public static void createPointOnTrip(String tripId, PointItem point, Context context, final ICallback<JSONObject> callback) {
        Ion.with(context).load(URL_CREATE_POINT_TRIP)
                .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                .setBodyParameter("tripId", tripId)
                .setBodyParameter("x", String.valueOf(point.getX_Lat()))
                .setBodyParameter("y", String.valueOf(point.getY_Long()))
                .setBodyParameter("z", String.valueOf(point.getZ()))
                .setBodyParameter("fromZ", String.valueOf(point.getFromZ()))
                .setBodyParameter("pointDescription", point.getPointDescription())
                .setBodyParameter("order", point.getOrder())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            JSONObject response_json = new JSONObject(result);
                            callback.onCompleted(response_json, null, null);
                        } catch (JSONException e1) {
                            callback.onCompleted(null, null, e1);
                        }
                    }
                });
    }

    //create trip
    /*
     * + url: /ITS/rest/trip/CreateTrip
	   + IRequest data: { tokenId: "...", startTime: "...", endTime:"...", from: "...", to: "..."}
	   + Response data: {code:“...”, description:“...”}
	 */
    public static void createTrip(Trip trip_item, Context context, final ICallback<JSONObject> callback) {
        Ion.with(context).load(URL_CREATE_TRIP)
                .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                .setBodyParameter("startTime", trip_item.getTimeStartTrip())
                .setBodyParameter("endTime", trip_item.getTimeEndTrip())
                .setBodyParameter("fromDescription", trip_item.getPlaceStartTrip())
                .setBodyParameter("toDescription", trip_item.getPlaceEndTrip())
                .setBodyParameter("tripName", trip_item.getDateTime())//span time of trip
                .setBodyParameter("privacy", trip_item.getPrivacy())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e == null) {
                            try {
                                callback.onCompleted(new JSONObject(result), null, null);
                            } catch (JSONException e1) {
                                callback.onCompleted(null, null, e1);
                            }
                        } else {
                            callback.onCompleted(null, null, e);
                        }
                    }
                });
    }

    public static ArrayList<MessageItem> getListCommentTrip(String tripId) {
        ArrayList<MessageItem> lstMessages = new ArrayList<>();
        try {
            JSONArray response_json = new JSONArray();
            // http://traffic.hcmut.edu.vn/ITS/rest/user/login

            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("tokenId", LoginManager.getInstance().getUser().getTokenId())
                    .addFormDataPart("tripId", tripId)
                    .build();
            String str_response;

            str_response = ApiCall.POST(client, URL_GET_COMMENTS_TRIP, requestBody);
            JSONObject jsonObj = new JSONObject(str_response);

            if (!jsonObj.isNull("listComment")) {
                response_json = new JSONArray(jsonObj.getString("listComment"));
            }

            // && !jsonObj.isNull("shareList")){
            for (int i = 0; i < response_json.length(); i++) {
                JSONObject jObj = new JSONObject(response_json.getString(i));
                MessageItem item = new MessageItem(jObj.getString("content"), R.drawable.user1, true, 10, jObj.getString("userId"), jObj.getString("dateTime"));
                lstMessages.add(item);
            }

            //arrange the list according to dateTime
            for (int i = 0; i < lstMessages.size() - 1; i++) {
                Date d1 = cse.its.helper.Utilities.convertStringToDateTime(lstMessages.get(i).getDate());
                if (d1 != null) {
                    for (int j = i + 1; j < lstMessages.size(); j++) {
                        Date d2 = cse.its.helper.Utilities.convertStringToDateTime(lstMessages.get(j).getDate());
                        if (d2.after(d1)) {
                            MessageItem tempMessegeItem = lstMessages.get(i);
                            lstMessages.set(i, lstMessages.get(j));
                            lstMessages.set(j, tempMessegeItem);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
        return lstMessages;
    }

    //get list friend
    /*
	 * + url:
	   + IRequest data:
	   + Response data:
	 */
    public static JSONArray getListFriend() {
        JSONArray response_json = new JSONArray();
        try {
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("tokenId", LoginManager.getInstance().getUser().getTokenId())
                    .build();
            String str_response;

            str_response = ApiCall.POST(client, URL_GET_FRIENDS, requestBody);

            JSONObject jsonObj = new JSONObject(str_response);
            if (!jsonObj.isNull("listFriend")) {
                response_json = new JSONArray(jsonObj.getString("listFriend"));
            }
            // if (response_json.isNull("tokenID")) {
            // String tokenId = response_json.get("tokenID").toString();
            // StaticVariable.user.setTokenId(tokenId);
            // }
            // if (response_json.isNull("status")) {
            // boolean status = response_json.getBoolean("status");
            // StaticVariable.user.setStatus(status);
            // }
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
        return response_json;
    }

    public static void getListPointOnTrip(String tripId, Context context, final ICallback<ArrayList<GeoPoint>> callback) {
        if(context !=null) {
            Ion.with(context).load(URL_GET_LIST_POINT_ON_TRIP)
                    .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                    .setBodyParameter("tripId", tripId)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String str_response) {
                            final ArrayList<GeoPoint> lstGeoPoints = new ArrayList<>();
                            try {
                                JSONArray listPoint = new JSONArray();
                                JSONObject jsonObj = new JSONObject(str_response);
                                if (!jsonObj.isNull("listPoint")) {
                                    listPoint = new JSONArray(jsonObj.getString("listPoint"));
                                }

                                MapFragment.listMarkerTrip = new ArrayList<>();

                                for (int i = 0; i < listPoint.length(); i++) {
                                    JSONObject pointJson = new JSONObject(listPoint.getString(i));
                                    if (!pointJson.isNull("x") && !pointJson.isNull("y")) {
                                        double x = Double.valueOf(pointJson.getString("x"));
                                        double y = Double.valueOf(pointJson.getString("y"));

                                        GeoPoint geoPoint = new GeoPoint(x, y);

                                        lstGeoPoints.add(geoPoint);

                                        JSONArray lstImg = new JSONArray(pointJson.getString("listImage"));

                                        for (int j =0; j< lstImg.length(); j++){
                                            JSONObject imgJSon = new JSONObject(lstImg.getString(i));
                                            MapFragment.MyMarker startMarker = new MapFragment.MyMarker(mMapView);
                                            startMarker.getMarker().setPosition(geoPoint);
                                            startMarker.setData(imgJSon.getString("url"));
                                            startMarker.setIndex(j);
                                            MapFragment.listMarkerTrip.add(startMarker);
                                        }

                                    }
                                }
                            } catch (Exception ex) {
                                Log.e(TAG, ex.getMessage());
                            }
                            callback.onCompleted(lstGeoPoints, null, e);
                        }
                    });
        }
    }

    /*
	 * + IRequest data (in json): { userId: "...", name: "...", frist_name:"...", last_name:"...",birthday:"...", email:"...", update_time:"...",gender:"...", local:"...", verified:"...", timezone:"...", link:"...", imei:"..."}
 	   + Response data: {tokenId:“...”, status:“...”
	 */
    public static void getListPrivateTrip(Context context, final ICallback<JSONArray> callback) {
        if(context !=null) {
            Ion.with(context).load(URL_GET_LIST_PRIVATE_TRIP)
                    .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String str_response) {
                            if (e == null) {
                                JSONObject jsonObj;
                                try {
                                    jsonObj = new JSONObject(str_response);
                                    if (!jsonObj.isNull("listTrip")) {
                                        callback.onCompleted(new JSONArray(jsonObj.getString("listTrip")), null, null);
                                    }
                                } catch (JSONException e1) {
                                    callback.onCompleted(null, null, e1);
                                }
                            } else {
                                callback.onCompleted(null, null, e);
                            }
                        }
                    });
        }
    }

    public static void searchTrip(String startPlace, String endPlace, String privacy, final ICallback<JSONArray> callback){
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tokenId", LoginManager.getInstance().getUser().getTokenId())
                .addFormDataPart("startPlace", startPlace)
                .addFormDataPart("endPlace", endPlace)
                .addFormDataPart("privacy", privacy)
                .build();
        try {
            String str_response  = ApiCall.POST(client, URL_GET_LIST_TRP_SEARCH, requestBody);
            JSONObject jsonObj = new JSONObject(str_response);
            try {
                if (!jsonObj.isNull("listTrip")) {
                    callback.onCompleted(new JSONArray(jsonObj.getString("listTrip")), null, null);
                }
            }
            catch(Exception ex) {
                callback.onCompleted(null, null, ex);
                ex.printStackTrace();
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    // service get List share Trip
	/*
	 * + IRequest data (in json): { userId: "...", name: "...", frist_name:"...", last_name:"...",birthday:"...", email:"...", update_time:"...",gender:"...", local:"...", verified:"...", timezone:"...", link:"...", imei:"..."}
 	   + Response data: {tokenId:“...”, status:“...”
	 */
    public static void getListSharedTrip(Context context, final ICallback<JSONArray> callback) {
        if(context !=null) {
            Ion.with(context).load(URL_GET_LIST_SHARE_TRIP)
                    .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String str_response) {
                            if (e == null) {
                                JSONArray response_json = new JSONArray();
                                try {
                                    JSONObject jsonObj = new JSONObject(str_response);
                                    if (!jsonObj.isNull("listTrip")) {
                                        response_json = new JSONArray(jsonObj.getString("listTrip"));
                                        callback.onCompleted(response_json, null, null);
                                    }
                                    // if (response_json.isNull("tokenID")) {
                                    // String tokenId = response_json.get("tokenID").toString();
                                    // StaticVariable.user.setTokenId(tokenId);
                                    // }
                                    // if (response_json.isNull("status")) {
                                    // boolean status = response_json.getBoolean("status");
                                    // StaticVariable.user.setStatus(status);
                                    // }
                                } catch (Exception ex) {
                                    Log.i(TAG, ex.getMessage());
                                    callback.onCompleted(null, null, ex);
                                }
                            } else {
                                callback.onCompleted(null, null, e);
                            }

                        }
                    });
        }
    }

    // service get List Trip
	/*
	 * + IRequest data (in json): { userId: "...", name: "...", frist_name:"...", last_name:"...",birthday:"...", email:"...", update_time:"...",gender:"...", local:"...", verified:"...", timezone:"...", link:"...", imei:"..."}
 	   + Response data: {tokenId:“...”, status:“...”
	 */
    public static void getListTrip(Context context, final ICallback<JSONArray> callback) {
        if(context !=null) {
            Ion.with(context).load(URL_GET_LIST_TRIP)
                    .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String str_response) {
                            if (e == null) {
                                JSONObject jsonObj;
                                try {
                                    jsonObj = new JSONObject(str_response);
                                    if (!jsonObj.isNull("listTrip")) {
                                        callback.onCompleted(new JSONArray(jsonObj.getString("listTrip")), null, null);
                                    }
                                } catch (JSONException e1) {
                                    callback.onCompleted(null, null, e1);
                                }
                            } else {
                                callback.onCompleted(null, null, e);
                            }
                        }
                    });
        }
    }

    // service get login
	/*
	 * + url:/ITS/rest/user/GETlogin/{userId}/{name}/{first_name}/{last_name}/ {birthday}/{email}/{update_time}/{gender}/{local}/{verified}/{timezone}/{link}/{imei}
	   + Response data: {tokenId:“...”, status:“...”
	 */
    public static JSONObject getLogin(User user) {
        JSONObject response_json = new JSONObject();
        String http_get_string = URL_GET_LOGIN + "/" + user.getId() + "/" + user.getName() + "/" + user.getFirst_name() +
                "/" + user.getLast_name() + "/" + user.getBirthday() + "/" + user.getEmail()
                + "/" + user.getUpdated_time() + "/" + user.getGender() + "/" + user.getLocal()
                + "/" + user.getVerified() + "/" + user.getTimezone() + "/" + user.getLink()
                + "/" + user.getImei();

        try {

            HttpConnection connection = new HttpConnection();
            connection.doGet(http_get_string);
            String str_response = connection.getContentAsString();

            response_json = new JSONObject(str_response);
            // if (response_json.isNull("tokenID")) {
            // String tokenId = response_json.get("tokenID").toString();
            // StaticVariable.user.setTokenId(tokenId);
            // }
            // if (response_json.isNull("status")) {
            // boolean status = response_json.getBoolean("status");
            // StaticVariable.user.setStatus(status);
            // }
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
        return response_json;
    }

    //get share on trip
	/*
	 * + url:
	   + IRequest data:
	   + Response data:
	 */
    public static void getShareOnTrip(String tripId, Context context, final ICallback<ArrayList<FriendItem>> callback) {
        Ion.with(context).load(URL_GET_SHARE_TRIP)
                .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                .setBodyParameter("tripId", tripId)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String str_response) {
                        if (e == null) {
                            try {
                                ArrayList<FriendItem> lstShareTrip = new ArrayList<>();
                                JSONArray jsonObjArray = new JSONArray(str_response);  //TODO: Danhbkit
                                for (int i = 0; i < jsonObjArray.length(); i++) {
                                    JSONObject jObj = new JSONObject(jsonObjArray.getString(i));

                                    lstShareTrip.add(new FriendItem(null, "", jObj.getString("userId"), false, ""));
                                }
                                callback.onCompleted(lstShareTrip, null, null);
                            } catch (JSONException e1) {
                                callback.onCompleted(null, null, e1);
                            }
                            //FIXME: parse array shareList
                        } else {
                            callback.onCompleted(null, null, e);
                        }
                    }
                });
    }

    //get trip info
	/*
		+ url: /ITS/rest/trip/GetTripInfo
		+ IRequest data: { tokenId: "...", tripId: "..."}
		+ Response data:
	 */
    public static JSONObject getTripInfo(String tripID, String tokenId) {
        JSONObject response_json = new JSONObject();
        try {
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("tokenId", LoginManager.getInstance().getUser().getTokenId())
                    .addFormDataPart("tripId", tripID)
                    .build();
            String str_response;

            str_response = ApiCall.POST(client, URL_GET_TRIP_INFO, requestBody);
            JSONObject jsonObj = new JSONObject(str_response);

            // if (response_json.isNull("tokenID")) {
            // String tokenId = response_json.get("tokenID").toString();
            // StaticVariable.user.setTokenId(tokenId);
            // }
            // if (response_json.isNull("status")) {
            // boolean status = response_json.getBoolean("status");
            // StaticVariable.user.setStatus(status);
            // }
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
        return response_json;
    }

    // service get user info
	/*
	 * + IRequest data (in json): { userId: "...", name: "...", frist_name:"...", last_name:"...",birthday:"...", email:"...", update_time:"...",gender:"...", local:"...", verified:"...", timezone:"...", link:"...", imei:"..."}
 	   + Response data: {tokenId:“...”, status:“...”
	 */
    public static void getUserInfo(String userId, Context context, final ICallback<JSONObject> callback) {
        Ion.with(context).load(URL_GET_USER_INFO)
                .setBodyParameter("userId", userId)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String str_response) {
                        if (e == null) {
                            try {
                                callback.onCompleted(new JSONObject(str_response), null, null);
                            } catch (Exception ex) {
                                callback.onCompleted(null, null, ex);
                            }
                        } else {
                            callback.onCompleted(null, null, e);
                        }
                    }
                });
    }

    //like trip
	/*
		+ url: /ITS/rest/like/LikeTrip
		+ IRequest data: { tokenId: "...", tripId:"..."}
		+ Response data: {code:“...”, description:“...”}
	 */
    public static void likeTrip(String tripID, Context context) {
        Ion.with(context).load(URL_LIKE_TRIP)
                .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                .setBodyParameter("tripId", tripID)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String str_response) {
                        if (e != null) {
                            Log.e(TAG, "likeTrip", e);
                        }
                    }
                });
    }

    // service login
    /*
     * + IRequest data (in json): { userId: "...", name: "...", frist_name:"...", last_name:"...",birthday:"...", email:"...", update_time:"...",gender:"...", local:"...", verified:"...", timezone:"...", link:"...", imei:"..."}
 	   + Response data: {tokenId:“...”, status:“...”
	 */
    @Deprecated
    public static JSONObject login() {
        JSONObject response_json = new JSONObject();
        try {
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("name", LoginManager.getInstance().getUser().getName())
                    .addFormDataPart("userId", LoginManager.getInstance().getUser().getId())
                    .addFormDataPart("firstName", LoginManager.getInstance().getUser().getFirst_name())
                    .addFormDataPart("lastName", LoginManager.getInstance().getUser().getLast_name())
                    .addFormDataPart("birthday", LoginManager.getInstance().getUser().getBirthday())
                    .addFormDataPart("email", LoginManager.getInstance().getUser().getEmail())
                    .addFormDataPart("updatedime", LoginManager.getInstance().getUser().getUpdated_time())
                    .addFormDataPart("gender", LoginManager.getInstance().getUser().getGender())
                    .addFormDataPart("local", LoginManager.getInstance().getUser().getLocal())
                    .addFormDataPart("verified", LoginManager.getInstance().getUser().getVerified())
                    .addFormDataPart("link", LoginManager.getInstance().getUser().getLink())
                    .addFormDataPart("timezone", LoginManager.getInstance().getUser().getTimezone())
                    .addFormDataPart("imei", LoginManager.getInstance().getUser().getImei())
                    .build();
            String str_response;

            str_response = ApiCall.POST(client, URL_LOGIN, requestBody);
            response_json = new JSONObject(str_response);

            // if (response_json.isNull("tokenID")) {
            // String tokenId = response_json.get("tokenID").toString();
            // StaticVariable.user.setTokenId(tokenId);
            // }
            // if (response_json.isNull("status")) {
            // boolean status = response_json.getBoolean("status");
            // StaticVariable.user.setStatus(status);
            // }
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
        return response_json;
    }

    // service logout
    /*
	 * + IRequest data (in json):
 	   + Response data:
	 */
    @Deprecated
    public static JSONObject logout() {
        JSONObject response_json = new JSONObject();
        try {
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("tokenId", LoginManager.getInstance().getUser().getTokenId())
                    .build();

            String str_response = ApiCall.POST(client, URL_LOGOUT, requestBody);

            response_json = new JSONObject(str_response);
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
        return response_json;
    }

    //save friend
	/*
	 * + url:
	   + IRequest data:
	   + Response data:
	 */
    //FIXME: correct
    public static JSONObject saveFriends(List<String> lstFriendId) {
        JSONObject response_json = new JSONObject();
        try {
            // http://traffic.hcmut.edu.vn/ITS/rest/user/login
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("tokenId", LoginManager.getInstance().getUser().getTokenId())
                    .addFormDataPart("fromSocialNetwork", LoginManager.getInstance().getUser().getTokenId())
                    .addFormDataPart("listFriend", lstFriendId.toString())
                    .build();

            String str_response = ApiCall.POST(client, URL_SAVE_FRIENDS, requestBody);

            response_json = new JSONObject(str_response);
            // if (response_json.isNull("tokenID")) {
            // String tokenId = response_json.get("tokenID").toString();
            // StaticVariable.user.setTokenId(tokenId);
            // }
            // if (response_json.isNull("status")) {
            // boolean status = response_json.getBoolean("status");
            // StaticVariable.user.setStatus(status);
            // }
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
        return response_json;
    }

    //save share on trip
	/*
	 * + url: 
	   + IRequest data:
	   + Response data: 
	 */
    public static void saveShareTrip(List<String> lstUserIdShare, String tripId, Context context, final ICallback<JSONObject> callback) {
        JSONArray arraySharedId = new JSONArray();
        for (int i = 0; i < lstUserIdShare.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", lstUserIdShare.get(i));
            arraySharedId.put(jsonObject.toString());
        }
        Ion.with(context).load(URL_SAVE_SHARE_TRIP)
                .setBodyParameter("tokenId", LoginManager.getInstance().getUser().getTokenId())
                .setBodyParameter("tripId", tripId)
                .setBodyParameter("shareList", arraySharedId.toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String str_response) {
                        if (e == null) {
                            try {
                                callback.onCompleted(new JSONObject(str_response), null, null);
                            } catch (JSONException e1) {
                                callback.onCompleted(null, null, e1);
                            }
                        } else {
                            callback.onCompleted(null, null, e);
                        }
                    }
                });
    }

    /*
    * KenK11
    * POST: Upload image
    * filename
    * pointID
    * tokenID
    * dataImage : image convert to string base64
    * */

    //TODO: DANHVT - CHANGE TO OKHTTP _ NOT CHECK
    public static JSONObject uploadImage(File file, String fileName, String MIME, String pointId) {
        String URL_UPLOAD = "http://traffic.hcmut.edu.vn/ITS/rest/upload/UploadImageToPoint";
        MediaType MEDIA_TYPE = MediaType.parse(MIME);
        String format = MIME.split("/")[1];
        JSONObject responseJson = new JSONObject();
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("filename", fileName)
                .addFormDataPart("pointId", pointId)
                .addFormDataPart("tokenId", LoginManager.getInstance().getUser().getTokenId())
                .addFormDataPart("dataImage", fileName+"."+format, RequestBody.create(MEDIA_TYPE, file))
                .build();
        String str_response;
        try {
            str_response = ApiCall.POST(client, URL_UPLOAD, requestBody);
            responseJson = new JSONObject(str_response);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return responseJson;
    }

    public static void uploadFile(final String filePath, final String pointId) {
        final String URL_UPLOAD = "http://traffic.hcmut.edu.vn/ITS/rest/upload/UploadImageToPoint";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                File f  = new File(filePath);
                String content_type  = getMimeType(f.getPath());
                String file_path = f.getAbsolutePath();
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);
                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("filename", filePath)
                        .addFormDataPart("pointId", pointId)
                        .addFormDataPart("tokenId", LoginManager.getInstance().getUser().getTokenId())
                        .addFormDataPart("dataImage",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                        .build();
                Request request = new Request.Builder()
                        .url(URL_UPLOAD)
                        .post(request_body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                    if(!response.isSuccessful()){
                        throw new IOException("Error : "+response);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
