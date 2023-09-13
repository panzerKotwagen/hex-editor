package gui.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class HexTableCellRenderer extends DefaultTableCellRenderer {
    //TODO: Add comments
    private static int findRow;
    private static int findCol;

    public static void setFindRow(int findRow) {
        HexTableCellRenderer.findRow = findRow;
    }

    public static void setFindCol(int findCol) {
        HexTableCellRenderer.findCol = findCol;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);

        HexTable fTable = (HexTable) table;
        boolean down = fTable.selectedRowIndexEnd > fTable.selectedRowIndexStart;

        if (row == findRow && column == findCol)
            cell.setBackground(new Color(0xF95D5D));
        else if (down) {
            if (fTable.selectedRowIndexStart < row
                    && row < fTable.selectedRowIndexEnd)
                cell.setBackground(new Color(0xA2DEEB));
            else if (row == fTable.selectedRowIndexStart && column >= fTable.selectedColIndexStart)
                cell.setBackground(new Color(0xA2DEEB));
            else if (row == fTable.selectedRowIndexEnd && column <= fTable.selectedColIndexEnd)
                cell.setBackground(new Color(0xA2DEEB));
            else
                cell.setBackground(new Color(0xFFFFFF));
        } else if (fTable.selectedRowIndexStart != fTable.selectedRowIndexEnd) {
            if (fTable.selectedRowIndexEnd < row
                    && row < fTable.selectedRowIndexStart)
                cell.setBackground(new Color(0xA2DEEB));
            else if (row == fTable.selectedRowIndexStart && column <= fTable.selectedColIndexStart)
                cell.setBackground(new Color(0xA2DEEB));
            else if (row == fTable.selectedRowIndexEnd && column >= fTable.selectedColIndexEnd)
                cell.setBackground(new Color(0xA2DEEB));
            else
                cell.setBackground(new Color(0xFFFFFF));
        } else if (isSelected) {
            cell.setBackground(new Color(0xA2DEEB));
        } else
            cell.setBackground(new Color(0xFFFFFF));

        return cell;
    }
}
