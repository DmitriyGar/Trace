/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trajectory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.List;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author user
 */
public class Trace {

    private double[][] xArray = null;
    private double W, L, kPixel = 0.303;
    private int i, k, j, c = 0,R=10, nTR = 0, nW = 4, nL = 5, xMin = 0, yMin = 0, nPixel = 100;
    private String strList[], tmp;
    private boolean timeGraphTrue = false;
    private BufferedImage bimW, bimR, bim, bim2;
    private Image img;
    private File fileCash, fileMap;
    private JLabel lab,lab1;
    private JFrame frame, frameIP;
    private JPanel panelL, panelR, panelIP;
    private Font font;
    private JButton butOpen, butSave, butIP, butResetIP, butSetIP;
    private ScrollPane sp;
    private JCheckBox cTime, cHD;
    private JCheckBox[] ip;
    private ButtonGroup rButGroup;
    private FileReader file0 = null, file1 = null, fileReadIP;
    private File fl = null, fileIP = null;
    private StringTokenizer sToken;
    private StringTokenizer[] sToken2;
    private JTextField txtField;
    private List listIP;
    private ArrayList<String> arrayNameIp, listArrayStr, arrayWip, arrayLip;
    private BasicStroke pen1 = new BasicStroke(3);

    Trace() {
        fileCash = new File("Cash0.png");
        // Show on frame
        frame = new JFrame("Траектория полета РН на карте мира");
        frameIP = new JFrame("Измерительные пункты");
        panelL = new JPanel();
        panelR = new JPanel();
        panelIP = new JPanel();
        cTime = new JCheckBox("TIME", false);
        cHD = new JCheckBox("HD map", false);
        txtField = new JTextField("100");
        listIP = new List(2, true);
        butOpen = new JButton("Открыть");
        butSave = new JButton("Сохранить");
        butIP = new JButton("ИП");
        butSetIP = new JButton("Принять");
        butIP.setEnabled(false);
        butResetIP = new JButton("Сбросить");
        butSave.setEnabled(false);
        sp = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
        frame.setLayout(null);
        panelL.setLayout(null);
        butOpen.setBounds(0, 10, 100, 30);
        panelL.add(butOpen);
        butSave.setBounds(0, 50, 100, 30);
        panelL.add(butSave);
        cTime.setBounds(0, 90, 70, 30);
        panelL.add(cTime);
        
        txtField.setBounds(0, 130, 50, 20);
        panelL.add(txtField);
        butIP.setBounds(0, 170, 100, 30);
        panelL.add(butIP);
        cHD.setBounds(0, 200, 100, 30);
        panelL.add(cHD);
        panelL.setBounds(2, 2, 100, 600);
        frame.add(panelL);
        frame.setSize(1370, 730);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Actions
        cTime.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (cTime.isSelected() == true) {
                    timeGraphTrue = true;
                } else {
                    timeGraphTrue = false;
                }
            }
        });
        cHD.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (cHD.isSelected() == true) {
                    kPixel=0.08;
                    R=40;
                } else {
                    kPixel=0.303;
                    R=10;
                }
            }
        });
        CaretListener txtLis = (new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                nPixel = Integer.parseInt(txtField.getText());
            }
        });
        ActionListener openLis = (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                open();
                paint();
                try {
                    bimR = ImageIO.read(fileCash);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Graphics2D g2 = bimR.createGraphics();
                if(kPixel==0.303){
                   lab = new JLabel(new ImageIcon(bimR)); 
                }else{
                   img=bimR.getScaledInstance(1250,650 , Image.SCALE_SMOOTH);
                lab = new JLabel(new ImageIcon(img)); 
                }
                sp.add(lab);
                sp.setVisible(true);
                sp.revalidate();
                sp.setBounds(5, 0, 1250, 650);
                panelR.add(sp);
                panelR.setBounds(80, 0, 1300, 700);
                frame.add(panelR);
                frame.repaint();
                frame.revalidate();
                butSave.setEnabled(true);
                butIP.setEnabled(true);
            }
        });
        ActionListener IPbutLis = (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameIP.setLayout(null);
                frameIP.setResizable(false);
                frameIP.add(butSetIP);
                butSetIP.setBounds(20, 5, 100, 20);
                frameIP.add(butResetIP);
                butResetIP.setBounds(150, 5, 100, 20);
                frameIP.add(listIP);
                listIP.setBounds(10, 30, 550, 620);
                readKatalogIP(); //загрузить католг ИПов из файла в список
                frameIP.setSize(600, 700);
                frameIP.setAlwaysOnTop(true);
                frameIP.setVisible(true);
                frameIP.setLocation(0, 0);

            }
        });
        ActionListener setIpLis = (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paintIp();
                frameIP.dispose();
                try {
                    bimR = ImageIO.read(fileCash);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Graphics2D g2 = bimR.createGraphics();
                if(kPixel==0.303){
                   lab = new JLabel(new ImageIcon(bimR)); 
                }else{
                   img=bimR.getScaledInstance(1250,650 , Image.SCALE_SMOOTH);
                lab = new JLabel(new ImageIcon(img)); 
                }
                sp.add(lab);
                sp.setVisible(true);
                sp.revalidate();
                sp.setBounds(5, 0, 1280, 650);
                panelR.add(sp);
                panelR.setBounds(100, 0, 1300, 700);
                frame.add(panelR);
                frame.repaint();
                frame.revalidate();
            }
        });
        ActionListener resetIpLis = (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listIP.removeAll();
                readKatalogIP();
            }
        });

        ActionListener saveLis = (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        butOpen.addActionListener(openLis);
        butIP.addActionListener(IPbutLis);
        txtField.addCaretListener(txtLis);
        butSave.addActionListener(saveLis);
        butSetIP.addActionListener(setIpLis);
        butResetIP.addActionListener(resetIpLis);
    }

    public static void main(String[] args) {
        Trace t = new Trace();
    }

    private void open() {
        System.out.println("  *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***");
        System.out.println("*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***");
        // читать данные с файла
        nTR = 0;
        c = 0;
        try {
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.setDialogTitle("ОТКРЫТЬ ФАЙЛ ТРАЕКТОРИИ ID56");
            int returnVal = fileOpen.showOpenDialog(fileOpen);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                fl = fileOpen.getSelectedFile();
            }
            System.out.println(fl);
            System.out.println("------------------------------------------------------------------");
            file0 = new FileReader(fl);
            file1 = new FileReader(fl);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<String> arrayL = new ArrayList<String>();
        ArrayList<String> arrayW = new ArrayList<String>();
        ArrayList<String> arrayT = new ArrayList<String>();
        Scanner s = null;
        //определение количества столбцов в файле
        try {
            s = new Scanner(file1);
            s.useLocale(Locale.US);
            s.nextLine();
            // System.out.println(s.nextLine()+" ");
            String str = s.nextLine();
            sToken = new StringTokenizer(str);
            while (sToken.hasMoreElements()) {
                sToken.nextElement();
                nTR++;
            }
        } finally {
            s.close();
        }
        try {
            s = new Scanner(file0);
            s.useLocale(Locale.US);
            W = s.nextDouble();
            L = s.nextDouble();
            System.out.println("START: " + W + " " + L);
            System.out.println("Количество столбцов траектории: " + nTR);
            s.nextLine();
            arrayL.clear();
            arrayW.clear();
            if (nTR == 13) {
                while (s.hasNextLine()) {
                    arrayT.add("" + s.nextDouble());
                    s.next();
                    s.next();
                    s.next();
                    s.next();
                    s.next();
                    s.next();
                    s.next();
                    s.next();
                    s.next();
                    arrayL.add("" + s.nextDouble());
                    arrayW.add("" + s.nextDouble());
                    s.next();
                    c++;
                    if (!s.hasNextDouble()) {
                        break;
                    }
                }
            } else if (nTR == 7) {
                while (s.hasNextLine()) {
                    arrayT.add("" + s.nextDouble());
                    s.next();
                    s.next();
                    s.next();
                    arrayL.add("" + s.nextDouble());
                    arrayW.add("" + s.nextDouble());
                    s.next();
                    c++;
                    if (!s.hasNextDouble()) {
                        break;
                    }
                }
            }
            xArray = new double[arrayL.size()][13];
        } finally {
            s.close();
        }
        for (i = 0; i < c; i++) {
            xArray[i][0] = 0;
            xArray[i][nW] = 0;
            xArray[i][nL] = 0;
        }
        for (i = 0; i < c; i++) {
            if (((int) W == (int) Double.parseDouble(arrayL.get(i))) && ((int) L == (int) Double.parseDouble(arrayW.get(i)))) {
                for (j = 0, k = i; k < c; k++, j++) {
                    xArray[j][nW] = Double.parseDouble(arrayL.get(k));
                    xArray[j][nL] = Double.parseDouble(arrayW.get(k));
                    xArray[j][0] = Double.parseDouble(arrayT.get(k));
                    if (xArray[j][nL] > 180) {
                        xArray[j][nL] = Double.parseDouble(arrayW.get(k)) - 360;
                    }
                }
                break;
            }
        }
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Количество точек: " + c);
    }

    private void paint() {
        if (cHD.isSelected() == true) {
                    fileMap = new File("maphd.jpg");
                } else {
                    fileMap = new File("map.png");
                }
        bimW = new BufferedImage(1450, 1000, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bimW.createGraphics();
        try {
            bimW = ImageIO.read(fileMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(bimW, "png", fileCash);
            bim = ImageIO.read(fileCash);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Graphics2D g22 = (Graphics2D) bim.createGraphics();

        if (kPixel==0.303) {
           pen1 = new BasicStroke(3);
            font = new Font(Font.SERIF, Font.BOLD, 16);
        } else {
           pen1 = new BasicStroke(8);
           font = new Font(Font.SERIF, Font.BOLD, 30);
        }
        if (kPixel == 0.303) {
            g22.translate(604, 312);
        } else {
            g22.translate(2329, 1216);
        }
        g22.setStroke(pen1);
       
        g22.setFont(font);
        //строится график
        if (nTR == 7) {
            for (i = 0; i < xArray.length / 2 - 1; i++) {
                if (xArray[i][nL] > 170 && xArray[i + 1][nL] < -170 || xArray[i][nL] < -170 && xArray[i + 1][nL] > 170) {
                    continue;
                }
                if (xArray[i][nL] > 0 && xArray[i + 1][nL] > 0 || xArray[i][nL] > -10 && xArray[i + 1][nL] < 10 || xArray[i][nL] < 0 && xArray[i + 1][nL] < 0) {
                    g22.setColor(Color.red);
                    g22.drawLine((int) (xArray[i][nL] / kPixel), (int) (-xArray[i][nW] / kPixel), (int) (xArray[i + 1][nL] / kPixel), (int) (-xArray[i + 1][nW] / kPixel));
                }
                g22.setColor(Color.blue);
                g22.fillOval((int) (xArray[0][nL] / kPixel) - R/2, (int) (-xArray[0][nW] / kPixel) - R/2, R, R);
            }
            if (timeGraphTrue == true) {
                for (i = 0; i < xArray.length / 2 - 1; i++) {
                    if (xArray[i][nL] > 170 && xArray[i + 1][nL] < -170 || xArray[i][nL] < -170 && xArray[i + 1][nL] > 170) {
                        continue;
                    }
                    if (xArray[i][nL] > 0 && xArray[i + 1][nL] > 0 || xArray[i][nL] > -10 && xArray[i + 1][nL] < 10 || xArray[i][nL] < 0 && xArray[i + 1][nL] < 0) {
                        if (Math.abs(Math.abs(xArray[i][nL] / kPixel) - Math.abs(xMin)) > nPixel
                                || Math.abs(Math.abs(-xArray[i][nW] / kPixel) - Math.abs(yMin)) > nPixel || i == xArray.length / 2 - 1) {
                            g22.setFont(font);
                            g22.setColor(Color.BLUE);
                            g22.drawString((int) xArray[i][0] + "", (int) (xArray[i][nL] / kPixel) - 5, (int) (-xArray[i][nW] / kPixel) - 5);
                            xMin = (int) (xArray[i][nL] / kPixel);
                            yMin = -(int) (xArray[i][nW] / kPixel);
                        }
                    }
                }
            }
        } else {
            for (i = 0; i < xArray.length - 1; i++) {
                g22.setColor(Color.red);
                if (xArray[i][nL] > 170 && xArray[i + 1][nL] < -170 || xArray[i][nL] < -170 && xArray[i + 1][nL] > 170) {
                    continue;
                }
                if (xArray[i][nL] > 0 && xArray[i + 1][nL] > 0 || xArray[i][nL] > -10 && xArray[i + 1][nL] < 10 || xArray[i][nL] < 0 && xArray[i + 1][nL] < 0) {
                    g22.drawLine((int) (xArray[i][nL] / kPixel), (int) (-xArray[i][nW] / kPixel), (int) (xArray[i + 1][nL] / kPixel), (int) (-xArray[i + 1][nW] / kPixel));
                }
                g22.setColor(Color.blue);
                g22.fillOval((int) (xArray[0][nL] / kPixel) - R/2, (int) (-xArray[0][nW] / kPixel) - R/2, R, R);
            }
            if (timeGraphTrue == true) {
                for (i = 0; i < xArray.length - 1; i++) {
                    if (xArray[i][nL] > 170 && xArray[i + 1][nL] < -170 || xArray[i][nL] < -170 && xArray[i + 1][nL] > 170) {
                        continue;
                    }

                    if (xArray[i][nL] > 0 && xArray[i + 1][nL] > 0 || xArray[i][nL] > -10 && xArray[i + 1][nL] < 10 || xArray[i][nL] < 0 && xArray[i + 1][nL] < 0) {
                        if (Math.abs(Math.abs(xArray[i][nL] / kPixel) - Math.abs(xMin)) > nPixel
                                || Math.abs(Math.abs(-xArray[i][nW] / kPixel) - Math.abs(yMin)) > nPixel || i == xArray.length - 1) {
                            g22.setFont(font);
                            g22.setColor(Color.BLUE);
                            g22.drawString((int) xArray[i][0] + "", (int) (xArray[i][nL] / kPixel) - 5, (int) (-xArray[i][nW] / kPixel) - 5);
                            xMin = (int) (xArray[i][nL] / kPixel);
                            yMin = (int) (xArray[i][nW] / kPixel);
                        }
                    }
                }
            }
        }
        try {
            ImageIO.write(bim, "png", fileCash);

        } catch (Exception e) {
            e.printStackTrace();
        }
        g22.dispose();
    }

    private void readKatalogIP() {
        try {
            fileIP = new File("katalogIP.dat");
            fileReadIP = new FileReader(fileIP);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        arrayWip = new ArrayList<String>();
        arrayLip = new ArrayList<String>();
        arrayNameIp = new ArrayList<String>();
        listArrayStr = new ArrayList<String>();
        Scanner s = null;
        //определение количества столбцов в файле 
        try {
            s = new Scanner(fileReadIP);
            s.useLocale(Locale.US);
            s.nextLine();
            while (s.hasNextLine()) {
                listArrayStr.add(s.nextLine());
            }
            sToken2 = new StringTokenizer[listArrayStr.size()];
            String strName = null, strW = null, tmp = null;
            for (i = 0; i < listArrayStr.size(); i++) {
                sToken2[i] = new StringTokenizer(listArrayStr.get(i));
                if (sToken2[i].nextElement().equals("name")) {
                    tmp = sToken2[i].nextToken();
                    while (tmp.length() < 15) {
                        tmp = tmp + " ";
                    }
                    continue;
                }
                sToken2[i].nextToken();
                arrayWip.add(sToken2[i].nextToken());
                arrayLip.add(sToken2[i].nextToken());
                arrayNameIp.add(tmp + " " + sToken2[i].nextToken());
            }
            listArrayStr.clear();
            for (i = 0; i < arrayLip.size(); i++) {
                strName = arrayNameIp.get(i);
                strW = arrayWip.get(i);
                while (strName.length() < 40) {
                    strName = strName + " ";
                }
                while (strW.length() < 8) {
                    strW = strW + " ";
                }
                listArrayStr.add(strName + "   " + strW + "    " + arrayLip.get(i));
                listIP.add(listArrayStr.get(i).toString());
            }
            font = new Font(Font.MONOSPACED, 0, 14);
            listIP.setFont(font);

        } finally {
            s.close();
        }
    }

    private void paintIp() {
        //считывание данных со списка и разбиение на массивы
        arrayWip.clear();
        arrayLip.clear();
        arrayNameIp.clear();
        listArrayStr.clear();
        sToken2[i] = null;
        listArrayStr.addAll(Arrays.asList(listIP.getSelectedItems()));
        for (i = 0; i < listArrayStr.size(); i++) {
            sToken2[i] = new StringTokenizer(listArrayStr.get(i));
            sToken2[i].nextToken();
            arrayNameIp.add(sToken2[i].nextToken());
            arrayWip.add(sToken2[i].nextToken());
            arrayLip.add(sToken2[i].nextToken());
            System.out.println(arrayNameIp.get(i) + "     " + arrayWip.get(i) + "    " + arrayLip.get(i));
        }
        //нанесение ИПов на карту по координатам
        try {
            bim2 = ImageIO.read(fileCash);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Color color = new Color(0, 0, 150);
        Graphics2D g22 = (Graphics2D) bim2.createGraphics();
        if (kPixel==0.303){
        g22.translate(604, 312);
        font = new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 18);
        }
        else {
            g22.translate(2329, 1216);
            font = new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 28);
        }
       
        g22.setFont(font);
        for (i = 0; i < arrayNameIp.size(); i++) {
            g22.setColor(Color.magenta);
            g22.fillOval((int) (Double.parseDouble(arrayLip.get(i)) / kPixel) - R/2, -(int) (Double.parseDouble(arrayWip.get(i)) / kPixel) - R/2, R, R);
            g22.setColor(color);
            g22.drawString(arrayNameIp.get(i) + "", (int) (Double.parseDouble(arrayLip.get(i)) / kPixel) - 5, -(int) (Double.parseDouble(arrayWip.get(i)) / kPixel) - 5);
        }
        try {
            ImageIO.write(bim2, "png", fileCash);

        } catch (Exception e) {
            e.printStackTrace();
        }
        g22.dispose();
        //sp.revalidate();
        //frame.repaint();
        //frame.revalidate();
    }

    private void save() {
        String fileName = null;
        //BufferedImage image = (BufferedImage) lab.createImage(lab.getWidth(), lab.getHeight());
        lab1=new JLabel();
       BufferedImage  image = new BufferedImage(1450, 1000, BufferedImage.TYPE_INT_ARGB);
        try {
            image = ImageIO.read(fileCash);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //image=(BufferedImage) img;
        Graphics2D gd2 = image.createGraphics();
      
        lab1.paint(gd2);
        try {
            JFileChooser jf = new JFileChooser();
            int result = jf.showSaveDialog(lab1);
            if (result == JFileChooser.APPROVE_OPTION) {
                fileName = jf.getSelectedFile().getAbsolutePath();
            }
            ImageIO.write(image, "png", new File(fileName + ".png"));
            System.out.println(fileName);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
