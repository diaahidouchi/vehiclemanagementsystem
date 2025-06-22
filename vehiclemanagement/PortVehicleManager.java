import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.GridLayout;

abstract class Vehicle {
     String owner;
     String brand;
     String model;
     String licensePlate;

    public Vehicle(String owner, String brand, String model, String licensePlate) {
        this.owner = owner;
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
    }

    public abstract String getDetails();
}

class Car extends Vehicle {
    final int maxSpeed;

    public Car(String owner, String brand, String model, String licensePlate, int maxSpeed) {
        super(owner, brand, model, licensePlate);
        this.maxSpeed = maxSpeed;
    }

    @Override
    public String getDetails() {
        return "Car - Owner: " + owner + ", Brand: " + brand + ", Model: " + model +
               ", License Plate: " + licensePlate + ", Max Speed: " + maxSpeed + " km/h";
    }
}

class Truck extends Vehicle {
    private boolean hasTrailer;

    public Truck(String owner, String brand, String model, String licensePlate, boolean hasTrailer) {
        super(owner, brand, model, licensePlate);
        this.hasTrailer = hasTrailer;
    }

    @Override
    public String getDetails() {
        return "Truck - Owner: " + owner + ", Brand: " + brand + ", Model: " + model +
               ", License Plate: " + licensePlate + ", Has Trailer: " + (hasTrailer ? "Yes" : "No");
    }
}

class Trip {
    private String tripId;
    private List<Vehicle> vehicles;

    public Trip(String tripId) {
        this.tripId = tripId;
        this.vehicles = new ArrayList<>();
    }
    
    public void addVehicle(Vehicle v) {
        vehicles.add(v);
    }
    
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void generateReport(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Trip ID: " + tripId);
            writer.println("List of Transported Vehicles:");
            writer.println("-----------------------------------");
            for (Vehicle v : vehicles) {
                writer.println(v.getDetails());
            }
            JOptionPane.showMessageDialog(null, "Report generated: " + filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing report: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

public class PortVehicleManager extends JFrame {
    private Trip currentTrip;
    private JTextArea displayArea;
    private JPanel inputPanel;
    private JTextField ownerField, brandField, modelField, licensePlateField, maxSpeedField;
    private JComboBox<String> vehicleTypeCombo;
    private JCheckBox trailerCheckBox;

    public PortVehicleManager() {
        setTitle("Port Vehicle Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Initialize components
        initializeComponents();
        setupLayout();
        
        // Create initial trip
        String tripId = JOptionPane.showInputDialog(this, "Enter Trip ID:");
        if (tripId != null && !tripId.trim().isEmpty()) {
            currentTrip = new Trip(tripId);
        } else {
            System.exit(0);
        }
    }

    private void initializeComponents() {
        // Display area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Input panel
        inputPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input fields
        ownerField = new JTextField();
        brandField = new JTextField();
        modelField = new JTextField();
        licensePlateField = new JTextField();
        maxSpeedField = new JTextField();
        
        String[] vehicleTypes = {"Car", "Truck"};
        vehicleTypeCombo = new JComboBox<>(vehicleTypes);
        trailerCheckBox = new JCheckBox("Has Trailer");

        // Add components to input panel
        inputPanel.add(new JLabel("Vehicle Type:"));
        inputPanel.add(vehicleTypeCombo);
        inputPanel.add(new JLabel("Owner:"));
        inputPanel.add(ownerField);
        inputPanel.add(new JLabel("Brand:"));
        inputPanel.add(brandField);
        inputPanel.add(new JLabel("Model:"));
        inputPanel.add(modelField);
        inputPanel.add(new JLabel("License Plate:"));
        inputPanel.add(licensePlateField);
        inputPanel.add(new JLabel("Max Speed (km/h):"));
        inputPanel.add(maxSpeedField);
        inputPanel.add(new JLabel("Trailer:"));
        inputPanel.add(trailerCheckBox);

        // Buttons
        JButton addButton = new JButton("Add Vehicle");
        JButton generateReportButton = new JButton("Generate Report");

        addButton.addActionListener(e -> addVehicle());
        generateReportButton.addActionListener(e -> generateReport());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(generateReportButton);

        // Add components to frame
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Update vehicle type selection
        vehicleTypeCombo.addActionListener(e -> updateInputFields());
        updateInputFields();
    }

    private void setupLayout() {
        setLocationRelativeTo(null);
    }

    private void updateInputFields() {
        boolean isCar = vehicleTypeCombo.getSelectedItem().equals("Car");
        maxSpeedField.setEnabled(isCar);
        trailerCheckBox.setEnabled(!isCar);
    }

    private void addVehicle() {
        try {
            String owner = ownerField.getText();
            String brand = brandField.getText();
            String model = modelField.getText();
            String licensePlate = licensePlateField.getText();

            if (owner.isEmpty() || brand.isEmpty() || model.isEmpty() || licensePlate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!");
                return;
            }

            Vehicle vehicle;
            if (vehicleTypeCombo.getSelectedItem().equals("Car")) {
                int maxSpeed = Integer.parseInt(maxSpeedField.getText());
                vehicle = new Car(owner, brand, model, licensePlate, maxSpeed);
            } else {
                boolean hasTrailer = trailerCheckBox.isSelected();
                vehicle = new Truck(owner, brand, model, licensePlate, hasTrailer);
            }

            currentTrip.addVehicle(vehicle);
            displayArea.append(vehicle.getDetails() + "\n");
            clearInputFields();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Max Speed!");
        }
    }

    private void clearInputFields() {
        ownerField.setText("");
        brandField.setText("");
        modelField.setText("");
        licensePlateField.setText("");
        maxSpeedField.setText("");
        trailerCheckBox.setSelected(false);
    }

    private void generateReport() {
        if (currentTrip != null) {
            currentTrip.generateReport("trip_report.txt");
            JOptionPane.showMessageDialog(this, "Report generated successfully: trip_report.txt");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PortVehicleManager().setVisible(true);
        });
    }
}
