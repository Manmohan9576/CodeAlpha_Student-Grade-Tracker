import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class GradeTrackerGUI extends JFrame {
    private JTextField nameField, gradesField;
    private JTextArea outputArea;
    private JButton addButton, showReportButton, saveButton, loadButton;
    private JButton editButton, deleteButton, tableButton;
    private ArrayList<Student> students;
    private JTable table;

    public GradeTrackerGUI() {
        setTitle("🎓 Student Grade Tracker");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        students = new ArrayList<>();

        // Inputs
        add(new JLabel("Student Name:"));
        nameField = new JTextField(20);
        add(nameField);

        add(new JLabel("Grades (comma separated):"));
        gradesField = new JTextField(20);
        add(gradesField);

        // Buttons
        addButton = new JButton("➕ Add Student");
        showReportButton = new JButton("📋 Show Report");
        saveButton = new JButton("💾 Save to File");
        loadButton = new JButton("📂 Load from File");
        editButton = new JButton("✏️ Edit Grades");
        deleteButton = new JButton("❌ Delete Student");
        tableButton = new JButton("🧾 Show Table");

        add(addButton);
        add(showReportButton);
        add(saveButton);
        add(loadButton);
        add(editButton);
        add(deleteButton);
        add(tableButton);

        // Output
        outputArea = new JTextArea(10, 55);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea));

        // Table
        table = new JTable();
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(650, 150));
        add(scrollPane);

        // Listeners
        addButton.addActionListener(e -> addStudent());
        showReportButton.addActionListener(e -> showReport());
        saveButton.addActionListener(e -> saveToFile());
        loadButton.addActionListener(e -> loadFromFile());
        editButton.addActionListener(e -> editStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        tableButton.addActionListener(e -> showTable());

        setVisible(true);
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String[] gradesText = gradesField.getText().split(",");

        if (name.isEmpty() || gradesText.length == 0) {
            JOptionPane.showMessageDialog(this, "Please enter name and grades.");
            return;
        }

        try {
            Student student = new Student(name);
            for (String g : gradesText) {
                student.grades.add(Integer.parseInt(g.trim()));
            }
            students.add(student);
            outputArea.append("✔️ Added: " + name + "\n");
            nameField.setText("");
            gradesField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Grades must be numbers (comma-separated).");
        }
    }

    private void showReport() {
        StringBuilder report = new StringBuilder("📋 STUDENT REPORT:\n\n");
        for (Student s : students) {
            report.append("Name: ").append(s.name).append("\n");
            report.append("Grades: ").append(s.grades).append("\n");
            report.append(String.format("Average: %.2f\n", s.getAverage()));
            report.append("Highest: ").append(s.getHighest()).append("\n");
            report.append("Lowest: ").append(s.getLowest()).append("\n");
            report.append("---------------\n");
        }
        outputArea.setText(report.toString());
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter("students.txt")) {
            for (Student s : students) {
                writer.print(s.name + ":");
                for (int g : s.grades) {
                    writer.print(g + ",");
                }
                writer.println();
            }
            JOptionPane.showMessageDialog(this, "Data saved to students.txt ✅");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file.");
        }
    }

    private void loadFromFile() {
        try {
            File file = new File("students.txt");
            Scanner reader = new Scanner(file);
            students.clear();

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] parts = line.split(":");
                if (parts.length != 2) continue;

                Student s = new Student(parts[0]);
                String[] gradeStrs = parts[1].split(",");
                for (String g : gradeStrs) {
                    if (!g.isEmpty()) s.grades.add(Integer.parseInt(g.trim()));
                }
                students.add(s);
            }

            reader.close();
            JOptionPane.showMessageDialog(this, "Data loaded from students.txt ✅");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading file.");
        }
    }

    private void editStudent() {
        String name = JOptionPane.showInputDialog(this, "Enter student name to edit:");
        if (name == null || name.trim().isEmpty()) return;

        for (Student s : students) {
            if (s.name.equalsIgnoreCase(name.trim())) {
                String newGrades = JOptionPane.showInputDialog(this, "Enter new grades (comma-separated):");
                if (newGrades == null) return;

                try {
                    s.grades.clear();
                    String[] gradesArray = newGrades.split(",");
                    for (String g : gradesArray) {
                        s.grades.add(Integer.parseInt(g.trim()));
                    }
                    JOptionPane.showMessageDialog(this, "Grades updated for " + s.name);
                    return;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid grade input.");
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Student not found.");
    }

    private void deleteStudent() {
        String name = JOptionPane.showInputDialog(this, "Enter student name to delete:");
        if (name == null || name.trim().isEmpty()) return;

        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            Student s = iterator.next();
            if (s.name.equalsIgnoreCase(name.trim())) {
                iterator.remove();
                JOptionPane.showMessageDialog(this, "Deleted student: " + s.name);
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Student not found.");
    }

    private void showTable() {
        String[] columnNames = { "Name", "Grades", "Average", "Highest", "Lowest" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Student s : students) {
            String gradesStr = s.grades.toString();
            Object[] row = {
                    s.name,
                    gradesStr,
                    String.format("%.2f", s.getAverage()),
                    s.getHighest(),
                    s.getLowest()
            };
            model.addRow(row);
        }

        table.setModel(model);
    }

    public static void main(String[] args) {
        new GradeTrackerGUI();
    }
}
