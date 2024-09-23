package estoque;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class Login {

    public static void main(String[] args) {
        // Cria a janela de login
        JFrame frame = new JFrame("Login LDAP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Centraliza a janela na tela

        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        
        JLabel labelUsuario = new JLabel("Usuário:");
        JTextField campoUsuario = new JTextField(20);
        JLabel labelSenha = new JLabel("Senha:");
        JPasswordField campoSenha = new JPasswordField(20);
        JButton btnLogin = new JButton("Entrar");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(labelUsuario, gbc);

        gbc.gridx = 1;
        panel.add(campoUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(labelSenha, gbc);

        gbc.gridx = 1;
        panel.add(campoSenha, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);

        
        btnLogin.addActionListener(e -> {
            String usuario = campoUsuario.getText();
            String senha = new String(campoSenha.getPassword());

            try {
                if (autenticarLdap(usuario, senha)) {
                    JOptionPane.showMessageDialog(frame, "Login bem-sucedido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose(); 

                    // Chama a MainView após login bem-sucedido
                    SwingUtilities.invokeLater(MainView::new);

                } else {
                    JOptionPane.showMessageDialog(frame, "Acesso negado. Usuário não pertence ao grupo GTIT_Redes.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NamingException ex) {
                JOptionPane.showMessageDialog(frame, "Erro de autenticação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    // Método para autenticar via LDAP e verificar se o usuário pertence ao grupo X
    private static boolean autenticarLdap(String usuario, String senha) throws NamingException {
        String ldapURL = "yourserver.com"; 
        String domain = "domain.br";
        String searchBase = "DC=domain,DC=br";
        String grupo = "CN=Group,OU=Group,OU=Local,DC=Domain,DC=br";

        
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, usuario + "@" + domain);
        env.put(Context.SECURITY_CREDENTIALS, senha); 

        
        DirContext ctx = new InitialDirContext(env);

        
        String searchFilter = "(&(objectClass=user)(sAMAccountName=" + usuario + "))";

        
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        
        NamingEnumeration<SearchResult> results = ctx.search(searchBase, searchFilter, searchControls);

        if (results.hasMore()) {
            SearchResult result = results.next();
            Attributes attrs = result.getAttributes();

            // Verifica se o usuário pertence ao grupo X
            Attribute memberOf = attrs.get("memberOf");
            if (memberOf != null) {
                for (int i = 0; i < memberOf.size(); i++) {
                    if (memberOf.get(i).toString().contains(grupo)) {
                        ctx.close();
                        return true; 
                    }
                }
            }
        }

        ctx.close();
        return false; 
    }
}
