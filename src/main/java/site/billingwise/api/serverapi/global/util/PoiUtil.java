package site.billingwise.api.serverapi.global.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

public class PoiUtil {

    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }

        throw new GlobalException(FailureInfo.INVALID_CELL_INPUT);
    }

    public static Integer getCellIntValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }

        throw new GlobalException(FailureInfo.INVALID_CELL_INPUT);
    }

    public static Long getCellLongValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        }

        throw new GlobalException(FailureInfo.INVALID_CELL_INPUT);
    }

    public static Boolean getCellBooleanValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        }

        throw new GlobalException(FailureInfo.INVALID_CELL_INPUT);
    }

}
