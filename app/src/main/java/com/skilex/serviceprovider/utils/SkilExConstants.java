package com.skilex.serviceprovider.utils;

/**
 * Created by Admin on 23-09-2017.
 */

public class SkilExConstants {

    //    URLS
    //    BASE URL
    private static final String BASE_URL = "https://skilex.in/";

    //BUILD URL
//    public static final String BUILD_URL = BASE_URL + "development/apisprovider/";
//    public static final String BUILD_URL = BASE_URL + "uat/apisprovider/";
    public static final String BUILD_URL = BASE_URL + "apisprovider/";

    //NUMBER VERIFICATION URL FOR LOGIN
    public static final String MOBILE_VERIFICATION = "mobile_check/";

    //Login type
    public static final String PREF_LOGIN_TYPE = "login_type";

    //Active status
    public static final String PREF_ACTIVE_STATUS = "activeStatus";

    //Service person
    public static final String PREF_SERVICE_PERSON_ID = "serv_pers_id";

    //Register with basic info
    public static final String REGISTER_SERVICE_PROVIDER = "register/";
    //Service provider registration basic field
    public static String REGISTRATION_NAME = "name";
    public static String REGISTRATION_PHONE_NUMBER = "mobile";
    public static String REGISTRATION_EMAIL_ID = "email";

    //List all categories
    public static final String LIST_ALL_CATEGORIES = "category_list/";

    //    Service Params
    public static String PARAM_MESSAGE = "msg";

    //     Shared preferences file name
    public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    //    Shared FCM ID
    public static final String KEY_FCM_ID = "fcm_id";

    //    Shared IMEI No
    public static final String KEY_IMEI = "imei_code";

    // Alert Dialog Constants
    public static String ALERT_DIALOG_TITLE = "alertDialogTitle";
    public static String ALERT_DIALOG_MESSAGE = "alertDialogMessage";
    public static String ALERT_DIALOG_TAG = "alertDialogTag";
    public static String ALERT_DIALOG_POS_BUTTON = "alert_dialog_pos_button";
    public static String ALERT_DIALOG_NEG_BUTTON = "alert_dialog_neg_button";

    //    Shared Phone No
    public static final String KEY_MOBILE_NUMBER = "mobile_number";

    //  Shared User Master Id
    public static final String KEY_USER_MASTER_ID = "user_master_id";

    //Select category
    public static final String KEY_USER_HAS_PREFERENCES = "hasPreferences";

    //    Shared UserID
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_TYPE = "user_type";
    //    Shared Categories Id
    public static final String KEY_CATEGORIES_ID = "category_ids";

    // Category save
    public static final String PROVIDER_CATEGORY_UPDATE = "serv_prov_category_add/";
    public static final String PERSON_CATEGORY_UPDATE = "serv_pers_category_add/";

    // Organization type
    public static final String ORGANIZATION_TYPE_SELECTION = "update_company_status/";
    public static final String KEY_TYPE_SELECTION = "company_status";

    // Individual type organization
    public static final String PROVIDER_INDIVIDUAL_DETAILS = "add_individual_status/";
    public static final String KEY_NO_OF_SERVICE_PERSON = "no_of_service_person";
    public static final String KEY_ALSO_A_SERVICE_PERSON = "also_service_person";

    //Registered organization
    public static final String PROVIDER_REGISTERED_ORG_DETAILS = "add_company_status/";
    public static final String KEY_COMPANY_NAME = "company_name";
    public static final String KEY_REG_NO_OF_PERSON = "no_of_service_person";
    public static final String KEY_COMPANY_ADDRESS = "company_address";
    public static final String KEY_COMPANY_CITY = "company_city";
    public static final String KEY_COMPANY_STATE = "company_state";
    public static final String KEY_COMPANY_ZIP = "company_zip";
    public static final String KEY_COMPANY_BUILDING_TYPE = "company_building_type";

    // Upload unregistered organization document upload
    public static final String UPLOAD_DOCUMENT = "upload_doc/";
    public static final String KEY_COMPANY_TYPE = "company_type";
    public static final String ID_PROOF_LIST = "list_idaddress_proofs/";
    public static final String UPDATE_UN_ORG_PROVIDER_BANK_INFO = "update_provider_bank_detail/";
    public static final String KEY_BANK_NAME = "bank_name";
    public static final String KEY_BANK_ACC_NO = "acc_no";
    public static final String KEY_BANK_BRANCH = "branch_name";
    public static final String KEY_BANK_IFSC = "ifsc_code";

    //Document verify status
    public static final String API_PROVIDER_DOCUMENT_STATUS = "list_provider_doc/";

    //Document success
    public static final String API_SET_PROVIDER_ACTIVE = "provider_active_status/";

    // Payment module
    public static final String PREF_PAYMENT_TYPE = "payment_type";

    //Requested services list
    public static final String API_REQUESTED_SERVICE = "list_requested_services/";

    //Requested services list
    public static final String API_ASSIGNED_SERVICE = "list_assigned_services/";

    //Accept service
    public static final String API_ACCEPT_SERVICE_ORDER = "accept_requested_services/";

    //Assigned service
    public static final String API_ASSIGN_SERVICE_ORDER = "assigned_accepted_services/";
    public static final String API_ASSIGNED_SERVICE_DETAILS = "detail_assigned_services/";

    //Cancel service
    public static final String API_CANCEL_REASON = "cancel_service_reasons/";
    public static final String API_CANCEL_SERVICE = "cancel_services/";
    public static String SERVICE_ORDER_ID = "service_order_id";
    public static String CANCEL_ID = "cancel_master_id";
    public static String CANCEL_COMMENTS = "comments";
    public static final String API_CANCELLED_SERVICE_LIST = "list_canceled_services/";

    //Ongoing service list
    public static final String API_ONGOING_SERVICE = "list_ongoing_services/";
    public static final String API_INITIATED_SERVICE_DETAIL = "detail_initiated_services/";
    public static final String API_ONGOING_SERVICE_DETAILS = "detail_ongoing_services/";
    public static final String API_ONGOING_SERVICE_DETAIL_UPDATE = "update_ongoing_services/";
    public static final String API_ONGOING_SERVICE_COMPLETE = "complete_services/";
    public static final String UPLOAD_BILL_DOCUMENT = "upload_service_bills/";

    //Completed service
    public static final String API_COMPLETED_SERVICE_LIST = "list_completed_services/";
    public static final String API_COMPLETED_SERVICE_DETAIL = "detail_completed_services/";

    public static String KEY_MATERIAL_NOTES = "material_notes";


    //Login
    public static String LOGIN = "login/";
    // Login Parameters
    public static String PHONE_NUMBER = "phone_no";
    public static String OTP = "otp";
    public static String DEVICE_TOKEN = "device_token";
    public static String MOBILE_TYPE = "mobile_type";
    public static String USER_MASTER_ID = "user_master_id";
    public static String SP_LAT = "lat";
    public static String SP_LON = "lon";
    public static String UNIQUE_NUMBER = "unique_number";
    public static String MOBILE_KEY = "mobile_key";
    public static String USER_STATUS = "user_stat";

    // Service person creation
    public static String API_SERVICE_PERSON_CREATION = "create_serv_person/";
    public static String API_SERVICE_PERSON_UPDATE = "update_serv_person_details/";
    public static String API_SERVICE_PERSON_LIST = "list_serv_persons/";


    public static String KEY_SERVICE_PERSON_ID = "serv_person_id";
    public static String KEY_FULL_NAME = "full_name";
    public static String KEY_GENDER = "gender";
    public static String KEY_ADDRESS = "address";
    public static String KEY_CITY = "city";
    public static String KEY_PIN_CODE = "pincode";
    public static String KEY_STATE = "state";
    public static String KEY_LANGUAGE_KNOWN = "language_known";
    public static String KEY_EDUCATION_QUALIFICATION = "edu_qualification";


    // Service provider active status
    public static String API_SERVICE_PROVIDER_STATUS = "provider_status/";

    //Login data
    public static final String PREF_FULL_NAME = "full_name";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_GENDER = "gender";
    public static final String PREF_PROFILE_PICTURE = "profile_picture";
    public static final String PREF_ADDRESS = "address";

    //PROFILE UPDATE URL
    public static final String PROFILE_INFO = "user_info/";
    public static final String UPDATE_PROFILE = "profile_update/";
    public static final String UPLOAD_IMAGE = "profile_pic_upload/";


}
