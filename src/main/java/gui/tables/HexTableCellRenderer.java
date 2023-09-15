package gui.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * The model for visually displaying the cell selection in a table
 * when they are selected sequentially without breaks, starting from
 * the first selected cell (anchor cell), ending with the last one on
 * which the user releases the mouse button (lead cell).
 */
public class HexTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);

        HexTable fTable = (HexTable) table;

        // User selection direction: top-down or bottom-up
        boolean down = fTable.selectedRowIndexEnd > fTable.selectedRowIndexStart;

        // If more than one line is selected and selection direction
        // is from top to bottom
        if (down) {
            // Select all the rows between the first and last
            if (fTable.selectedRowIndexStart < row
                    && row < fTable.selectedRowIndexEnd)
                cell.setBackground(new Color(0xA2DEEB));
            // Select all the cells that are to the right of anchor cell
            else if (row == fTable.selectedRowIndexStart
                    && column >= fTable.selectedColIndexStart)
                cell.setBackground(new Color(0xA2DEEB));
            // Select all the cells that are to the left of lead cell
            else if (row == fTable.selectedRowIndexEnd
                    && column <= fTable.selectedColIndexEnd)
                cell.setBackground(new Color(0xA2DEEB));
            else
                cell.setBackground(new Color(0xFFFFFF));

        // If more than one line is selected and selection direction
        // is from bottom to top
        } else if (fTable.selectedRowIndexStart != fTable.selectedRowIndexEnd) {
            // Select all the rows between the first and last
            if (fTable.selectedRowIndexEnd < row
                    && row < fTable.selectedRowIndexStart)
                cell.setBackground(new Color(0xA2DEEB));
            // Select all the cells that are to the left of anchor cell
            else if (row == fTable.selectedRowIndexStart
                    && column <= fTable.selectedColIndexStart)
                cell.setBackground(new Color(0xA2DEEB));
            // Select all the cells that are to the right of lead cell
            else if (row == fTable.selectedRowIndexEnd
                    && column >= fTable.selectedColIndexEnd)
                cell.setBackground(new Color(0xA2DEEB));
            else
                cell.setBackground(new Color(0xFFFFFF));

        // If only one line is selected
        } else if (isSelected) {
            cell.setBackground(new Color(0xA2DEEB));
        } else
            cell.setBackground(new Color(0xFFFFFF));

        return cell;
    }
}
