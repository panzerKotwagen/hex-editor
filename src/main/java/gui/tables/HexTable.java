package gui.tables;

import editor.ByteSequence;
import editor.HexEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Describes the table to display the contents of a file in binary
 * format.
 */
public class HexTable extends JTable {

    private static final int OFFSET_COLUMN_WIDTH = 120;

    private static final int BYTE_COLUMN_WIDTH = 50;

    private static final int SCROLL_BAR_WIDTH = 20;

    /**
     * The index of the anchor selected row.
     */
    public int selectedRowIndexStart;

    /**
     * The index of the lead selected row.
     */
    public int selectedRowIndexEnd;

    /**
     * The index of the anchor selected column.
     */
    public int selectedColIndexStart;

    /**
     * The index of the lead selected column.
     */
    public int selectedColIndexEnd;

    /**
     * Constructs a HexTable that is initialized with tableModel as
     * the data model.
     *
     * @param tableModel the data model for the table
     */
    public HexTable(HexTableModel tableModel) {
        super(tableModel);

        setRowHeight(40);

        setIntercellSpacing(new Dimension(10, 10));

        setShowGrid(false);

        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        setCellSelectionEnabled(true);

        getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        getColumnModel().getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        setColumnsWidth();

        getTableHeader().setReorderingAllowed(false);

        setDefaultRenderer(Number.class, new CustomTableCellRenderer());

        getSelectionModel().addListSelectionListener(
                e -> updateSelectionIndexes());

        getColumnModel().getSelectionModel().addListSelectionListener(
                e -> updateSelectionIndexes());
    }

    /**
     * Sets preferred width for every column in the given table.
     */
    public static void setColumnsWidth(JTable table, int width) {
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(width);
        }
    }

    /**
     * Creates a table containing the data of the given file.
     */
    public static HexTable createTable(HexEditor dataSource) {
        HexTableModel model = new HexTableModel(16);
        model.setDataSource(dataSource);
        return new HexTable(model);
    }

    /**
     * Changes the table structure according to the width of the
     * application window.
     *
     * @param frameWidth the width of the app window
     */
    public void updateTableView(int frameWidth) {
        int columnCount = (frameWidth - OFFSET_COLUMN_WIDTH - SCROLL_BAR_WIDTH)
                / BYTE_COLUMN_WIDTH;

        if (columnCount < 16) {
            columnCount = 16;
            this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } else {
            this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        }

        HexTableModel tableModel = (HexTableModel) this.getModel();
        tableModel.setColumnCount(columnCount);
        setColumnsWidth();
    }

    /**
     * Sets the specified column width for instance of the HexTable.
     */
    public void setColumnsWidth() {
        setColumnsWidth(this, BYTE_COLUMN_WIDTH);
        this.getColumnModel().getColumn(0).setPreferredWidth(
                OFFSET_COLUMN_WIDTH);
    }

    /**
     * Updates selected cell indexes.
     */
    public void updateSelectionIndexes() {
        selectedRowIndexStart = getSelectionModel().getAnchorSelectionIndex();
        selectedRowIndexEnd = getSelectionModel().getLeadSelectionIndex();
        selectedColIndexStart = getColumnModel().getSelectionModel()
                .getAnchorSelectionIndex();
        selectedColIndexEnd = getColumnModel().getSelectionModel()
                .getLeadSelectionIndex();
    }

    /**
     * Returns the ByteSequence of length 8 which filling with the
     * bytes from the cells starting from the lead selected cell.
     * If there are no enough cells to the right of the lead selected
     * cell then returns sequence of less length.
     */
    public ByteSequence getByteSequence() {
        byte[] array = new byte[8];
        HexTableModel tableModel = (HexTableModel) this.getModel();

        for (int i = 0; i < 8; i++) {
            try {
                int index = tableModel.getIndex(
                        this.selectedRowIndexEnd,
                        this.selectedColIndexEnd);
                array[i] = tableModel.getValueByIndex(index);
            }
            // If the number of bytes in the file starting from the
            // selected position is less than 8
            catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        return new ByteSequence(array);
    }

    /**
     * Overridden to forbid the multiple selection when pressing ctrl
     * and selection the offset column.
     */
    @Override
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        if (columnIndex == 0)
            return;
        super.changeSelection(rowIndex, columnIndex, false, extend);
    }
}

class CustomTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);

        HexTable fTable = (HexTable) table;
        boolean down = fTable.selectedRowIndexEnd > fTable.selectedRowIndexStart;

        if (down) {
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