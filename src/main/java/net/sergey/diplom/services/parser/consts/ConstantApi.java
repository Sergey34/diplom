package net.sergey.diplom.services.parser.consts;

import net.sergey.diplom.dto.messages.Message;

import static net.sergey.diplom.dto.messages.Message.SC_NOT_ACCEPTABLE;
import static net.sergey.diplom.dto.messages.Message.SC_OK;

public class ConstantApi {
    public static final Message EMPTY_AIRFOIL_SHORT_NAME = new Message("Ошибка при добавлении в базу нового airfoil. Короткое имя профиля не должно быть пустым", SC_NOT_ACCEPTABLE);
    public static final Message ASS_SUCCESS = new Message("Airfoil успешно добален / обновлен", SC_OK);
    public static final Message DONE = new Message("done", SC_OK);
    public static final String CL_V_CD = "Cl v Cd";
    public static final String CL_V_ALPHA = "Cl v Alpha";
    public static final String CD_V_ALPHA = "Cd v Alpha";
    public static final String CM_V_ALPHA = "Cm v Alpha";
    public static final String CL_DIV_CD_V_ALPHA = "Cl div Cd v Alpha";
    public static String GET_FILE_CSV = "http://airfoiltools.com/polar/csv?polar=";//xf-a18-il-50000
    public static String GET_DETAILS = "http://airfoiltools.com/airfoil/details?airfoil=";//a18-il
    public static String GET_LIST_AIRFOIL_BY_PREFIX = "http://airfoiltools.com/search/list?page=";//a //A
    public static String GET_COORDINATE_VIEW = "http://airfoiltools.com/airfoil/seligdatfile?airfoil=";//a18sm-il

    private ConstantApi() {
    }
}
