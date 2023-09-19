package gui.tables;

import editor.ByteSequence;
import editor.HexEditor;


import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;

/**
 * Describes the table with dynamic column count to display the
 * contents of a file in binary format.
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
     * The {@code HexTableModel} that provides the data displayed
     * by this {@code HexTable}.
     */
    private final HexTableModel tableModel;

    /**
     * Constructs a HexTable that is initialized with HexTableModel as
     * the data model.
     *
     * @param tableModel the data model for the table
     */
    public HexTable(HexTableModel tableModel) {
        super(tableModel);

        this.tableModel = tableModel;

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

        setDefaultRenderer(Number.class, new HexTableCellRenderer());

        getSelectionModel().addListSelectionListener(
                e -> updateSelectionIndexes());

        getColumnModel().getSelectionModel().addListSelectionListener(
                e -> updateSelectionIndexes());
    }

    /**
     * Overridden to forbid the multiple selection when pressing ctrl
     * and selection the offset column.
     */
    @Override
    public void changeSelection(int rowIndex, int columnIndex,
                                boolean toggle, boolean extend) {
        if (columnIndex == 0)
            return;
        super.changeSelection(rowIndex, columnIndex, false, extend);
    }

    /**
     * Returns the {@code HexTableModel} that provides the data
     * displayed by this {@code HexTable}.
     *
     * @return the {@code HexTableModel} that provides the data
     * displayed by this {@code HexTable}
     */
    @Override
    public HexTableModel getModel() {
        return (HexTableModel) super.getModel();
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
     * Sets preferred width for every column in the given table.
     */
    public static void setColumnsWidth(JTable table, int width) {
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(width);
        }
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
     * Changes the table structure according to the width of the
     * application window.
     *
     * @param frameWidth the width of the app window
     */
    public void updateTableView(int frameWidth) {
        int newColumnCount = (frameWidth - OFFSET_COLUMN_WIDTH - SCROLL_BAR_WIDTH)
                / BYTE_COLUMN_WIDTH;

        if (newColumnCount == this.getColumnCount())
            return;

        if (newColumnCount < 16) {
            newColumnCount = 16;
            this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } else {
            this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        }

        tableModel.setColumnCount(newColumnCount);

        setColumnsWidth();
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
     * Returns the byte offset in the file that is corresponds to the
     * anchor selected cell.
     */
    public int getStartOffset() {
        return tableModel.getOffset(selectedRowIndexStart, selectedColIndexStart);
    }

    /**
     * Returns the byte offset in the file that is corresponds to the
     * lead selected cell.
     */
    public int getEndOffset() {
        return tableModel.getOffset(selectedRowIndexEnd, selectedColIndexEnd);
    }

    /**
     * Returns the ByteSequence of length 8 which filling with the
     * bytes from the cells starting from the lead selected cell.
     * If there are no enough cells to the right of the lead selected
     * cell then returns sequence of less length.
     */
    public ByteSequence getByteSequence() {
        return tableModel.getByteSequence(getEndOffset());
    }
}


