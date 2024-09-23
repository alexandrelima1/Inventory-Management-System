package estoque;

import estoque.MainView;

import javax.swing.*;
import java.awt.*;

public class Start extends JPanel {

    public Start(MainView mainView) {
        setLayout(new BorderLayout());

        
        setBackground(new Color(189, 195, 199)); 

        
        JPanel presentationPanel = new JPanel();
        presentationPanel.setLayout(new GridBagLayout());
        presentationPanel.setOpaque(false); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;


        JLabel titleLabel = new JLabel("Sistema Gestão de Estoque - Conex");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(44, 62, 80)); 
        presentationPanel.add(titleLabel, gbc);

        
        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Clique para iniciar");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(127, 140, 141)); 
        presentationPanel.add(subtitleLabel, gbc);

        
        add(presentationPanel, BorderLayout.CENTER);

        
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainView.exibirTabbedPane(); 
            }
        });
    }
}
