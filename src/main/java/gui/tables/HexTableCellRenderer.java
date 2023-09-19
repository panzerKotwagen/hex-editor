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
    public Component getTableCellRendererComponent(
            JTable table, Object obj,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        Component cell = super.getTableCellRendererComponent(
                table, obj, isSelected, hasFocus, row, column);

        HexTable hexTable = (HexTable) table;
        HexTableModel tableModel = (HexTableModel) table.getModel();

        int start = hexTable.getStartOffset();
        int end = hexTable.getEndOffset();
        int cellOffset = tableModel.getOffset(row, column);

        if (cellOffset >= Math.min(start, end)
                && cellOffset <= Math.max(start, end)) {
            cell.setBackground(new Color(0xA2DEEB));
        } else {
            cell.setBackground(new Color(0xFFFFFF));
        }

        return cell;
    }
}
