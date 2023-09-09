package gui.window;

import editor.ByteSequence;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * The panel on which byte decode is placed.
 */
public class ByteRepresentPanel extends JPanel {

    /**
     * Text fields in which byte block represented as number.
     */
    private final HashMap<String, JTextField> textFields = new HashMap<>();

    /**
     * Labels for the bit represent panel.
     */
    private final String[] labelTexts = {
            "Signed 8 bit", "Signed 32 bit", "Unsigned 8 bit",
            "Unsigned 32 bit", "Signed 16 bit", "Signed 64 bit",
            "Unsigned 16 bit", "Unsigned 64 bit", "Float 32 bit",
            "Double 64 bit"};

    /**
     * Constructs the panel.
     */
    public ByteRepresentPanel() {
        super(new GridLayout(5, 4, 5, 5));
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        makeByteRepresentPanel();
    }

    /**
     * Makes the panel on which byte represent values are placed on.
     */
    private void makeByteRepresentPanel() {
        for (int i = 0; i < 10; i++) {
            JLabel label = new JLabel(labelTexts[i]);
            label.setHorizontalAlignment(JLabel.RIGHT);
            JTextField textField = new JTextField();
            textField.setEnabled(false);
            textField.setDisabledTextColor(Color.BLACK);
            textFields.put(labelTexts[i], textField);
            this.add(label);
            this.add(textField);
        }
    }

    /**
     * Fills the byte represent panel with the values of byte block.
     */
    public void fillPane(ByteSequence byteSequence) {
        textFields.get("Signed 8 bit").setText(String.valueOf(
                byteSequence.representAsSigned8Bit(0)));
        textFields.get("Unsigned 8 bit").setText(String.valueOf(
                byteSequence.representAsUnsigned8Bit(0)));
        textFields.get("Signed 16 bit").setText(String.valueOf(
                byteSequence.representAsSigned16Bit(0)));
        textFields.get("Unsigned 16 bit").setText(String.valueOf(
                byteSequence.representAsUnsigned16Bit(0)));
        textFields.get("Signed 32 bit").setText(String.valueOf(
                byteSequence.representAsSigned32Bit(0)));
        textFields.get("Unsigned 32 bit").setText(String.valueOf(
                byteSequence.representAsUnsigned32Bit(0)));
        textFields.get("Signed 64 bit").setText(String.valueOf(
                byteSequence.representAsSigned64Bit(0)));
        textFields.get("Unsigned 64 bit").setText(String.valueOf(
                byteSequence.representAsUnsigned64Bit(0)));
        textFields.get("Float 32 bit").setText(String.valueOf(
                byteSequence.representAsFloat(0)));
        textFields.get("Double 64 bit").setText(String.valueOf(
                byteSequence.representAsDouble(0)));
    }
}
