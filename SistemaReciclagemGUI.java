import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

// Padrão Observer para notificar sobre eventos de reciclagem
interface LixeiraListener {
    void onResiduoDescartado(Residuo residuo);
}

// Padrão State para gerenciar o estado do resíduo
interface EstadoResiduo {
    void validar(Residuo residuo, Usuario usuario);
    void processarRecompensa(Residuo residuo, Usuario usuario);
}

// Estados concretos
class EstadoPendente implements EstadoResiduo {
    @Override
    public void validar(Residuo residuo, Usuario usuario) {
        ValidadorResiduo validador = new ValidadorTipoMaterial(
            new ValidadorPesoMinimo(
                new ValidadorBasico()
            )
        );
        
        if (validador.validar(residuo)) {
            residuo.setEstado(new EstadoAprovado());
        } else {
            residuo.setEstado(new EstadoRejeitado());
        }
        residuo.getEstado().validar(residuo, usuario);
    }

    @Override
    public void processarRecompensa(Residuo residuo, Usuario usuario) {
        System.out.println("Resíduo ainda não validado");
    }
}

class EstadoValidando implements EstadoResiduo {
    @Override
    public void validar(Residuo residuo, Usuario usuario) {
        // Lógica de validação já foi executada
    }

    @Override
    public void processarRecompensa(Residuo residuo, Usuario usuario) {
        System.out.println("Resíduo em processo de validação");
    }
}

class EstadoAprovado implements EstadoResiduo {
    @Override
    public void validar(Residuo residuo, Usuario usuario) {
        // Não precisa validar novamente
    }

    @Override
    public void processarRecompensa(Residuo residuo, Usuario usuario) {
        int moedas = SistemaRecompensas.getInstance()
            .calcularRecompensa(residuo.getTipo(), residuo.getPeso());
        usuario.adicionarMoedas(moedas);
        residuo.setProcessado(true);
        System.out.println(usuario.getNome() + " recebeu " + moedas + " moedas!");
    }
}

class EstadoRejeitado implements EstadoResiduo {
    @Override
    public void validar(Residuo residuo, Usuario usuario) {
        // Não pode ser validado
    }

    @Override
    public void processarRecompensa(Residuo residuo, Usuario usuario) {
        System.out.println("Resíduo rejeitado - sem recompensa");
    }
}

// Padrão Decorator para validação
interface ValidadorResiduo {
    boolean validar(Residuo residuo);
}

class ValidadorBasico implements ValidadorResiduo {
    @Override
    public boolean validar(Residuo residuo) {
        return residuo != null && residuo.getPeso() > 0;
    }
}

abstract class ValidadorDecorator implements ValidadorResiduo {
    protected ValidadorResiduo validador;
    
    public ValidadorDecorator(ValidadorResiduo validador) {
        this.validador = validador;
    }
    
    @Override
    public boolean validar(Residuo residuo) {
        return validador.validar(residuo);
    }
}

class ValidadorTipoMaterial extends ValidadorDecorator {
    public ValidadorTipoMaterial(ValidadorResiduo validador) {
        super(validador);
    }
    
    @Override
    public boolean validar(Residuo residuo) {
        return super.validar(residuo) && 
               residuo.getTipo() != null &&
               !residuo.getTipo().isEmpty();
    }
}

class ValidadorPesoMinimo extends ValidadorDecorator {
    private static final double PESO_MINIMO = 0.1;
    
    public ValidadorPesoMinimo(ValidadorResiduo validador) {
        super(validador);
    }
    
    @Override
    public boolean validar(Residuo residuo) {
        return super.validar(residuo) && 
               residuo.getPeso() >= PESO_MINIMO;
    }
}

// Padrão Strategy para cálculo de recompensas
interface CalculadoraRecompensa {
    int calcular(double peso);
}

class CalculadoraPlastico implements CalculadoraRecompensa {
    @Override
    public int calcular(double peso) {
        return (int) (peso * 10); // 10 moedas por kg
    }
}

class CalculadoraVidro implements CalculadoraRecompensa {
    @Override
    public int calcular(double peso) {
        return (int) (peso * 8); // 8 moedas por kg
    }
}

class CalculadoraMetal implements CalculadoraRecompensa {
    @Override
    public int calcular(double peso) {
        return (int) (peso * 12); // 12 moedas por kg
    }
}

class CalculadoraPapel implements CalculadoraRecompensa {
    @Override
    public int calcular(double peso) {
        return (int) (peso * 6); // 6 moedas por kg
    }
}

// Singleton para gerenciar recompensas
class SistemaRecompensas {
    private static SistemaRecompensas instance;
    private Map<String, CalculadoraRecompensa> estrategias;
    
    private SistemaRecompensas() {
        estrategias = new HashMap<>();
        estrategias.put("PLÁSTICO", new CalculadoraPlastico());
        estrategias.put("VIDRO", new CalculadoraVidro());
        estrategias.put("METAL", new CalculadoraMetal());
        estrategias.put("PAPEL", new CalculadoraPapel());
    }
    
    public static synchronized SistemaRecompensas getInstance() {
        if (instance == null) {
            instance = new SistemaRecompensas();
        }
        return instance;
    }
    
    public int calcularRecompensa(String tipo, double peso) {
        CalculadoraRecompensa estrategia = estrategias.get(tipo.toUpperCase());
        if (estrategia != null) {
            return estrategia.calcular(peso);
        }
        return 0;
    }
}

// Classes de domínio
class Residuo {
    private String tipo;
    private double peso;
    private EstadoResiduo estado;
    private boolean processado;
    
    public Residuo(String tipo, double peso) {
        this.tipo = tipo;
        this.peso = peso;
        this.estado = new EstadoPendente();
        this.processado = false;
    }
    
    public void validar(Usuario usuario) {
        estado.validar(this, usuario);
    }
    
    public void processarRecompensa(Usuario usuario) {
        estado.processarRecompensa(this, usuario);
    }

    // Getters e setters
    public String getTipo() { return tipo; }
    public double getPeso() { return peso; }
    public EstadoResiduo getEstado() { return estado; }
    public void setEstado(EstadoResiduo estado) { this.estado = estado; }
    public boolean isProcessado() { return processado; }
    public void setProcessado(boolean processado) { this.processado = processado; }
}

class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private int moedas;

    public Usuario(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.moedas = 0;
    }

    public void adicionarMoedas(int quantidade) {
        moedas += quantidade;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getMoedas() { return moedas; }
}

// Lixeira inteligente com Observer
class LixeiraInteligente {
    private String tipo;
    private double capacidade;
    private List<LixeiraListener> listeners = new ArrayList<>();
    
    public LixeiraInteligente(String tipo, double capacidade) {
        this.tipo = tipo;
        this.capacidade = capacidade;
    }
    
    public void addListener(LixeiraListener listener) {
        listeners.add(listener);
    }
    
    public void removerListener(LixeiraListener listener) {
        listeners.remove(listener);
    }
    
    public void descartarResiduo(Residuo residuo, Usuario usuario) throws LixeiraException {
        if (!residuo.getTipo().equalsIgnoreCase(tipo)) {
            throw new LixeiraException("Tipo de resíduo incorreto para esta lixeira");
        }
        
        if (residuo.getPeso() > capacidade) {
            throw new LixeiraException("Capacidade da lixeira excedida");
        }
        
        notificarListeners(residuo);
        residuo.validar(usuario);
    }
    
    private void notificarListeners(Residuo residuo) {
        for (LixeiraListener listener : listeners) {
            listener.onResiduoDescartado(residuo);
        }
    }
}

// Exceções personalizadas
class LixeiraException extends Exception {
    public LixeiraException(String message) {
        super(message);
    }
}

class SistemaArquivosException extends Exception {
    public SistemaArquivosException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Gerenciador de persistência
class GerenciadorUsuarios {
    private static final String ARQUIVO_USUARIOS = "usuarios.dat";
    
    public void salvarUsuarios(List<Usuario> usuarios) throws SistemaArquivosException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_USUARIOS))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            throw new SistemaArquivosException("Erro ao salvar usuários", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Usuario> carregarUsuarios() throws SistemaArquivosException {
        File arquivo = new File(ARQUIVO_USUARIOS);
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (List<Usuario>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SistemaArquivosException("Erro ao carregar usuários", e);
        }
    }
}

// Interface gráfica
public class SistemaReciclagemGUI extends JFrame implements LixeiraListener {
    private final GerenciadorUsuarios gerenciadorUsuarios;
    private final List<Usuario> usuarios;
    private final Map<String, LixeiraInteligente> lixeiras;
    private Usuario usuarioAtual;
    
    // Componentes da UI
    private JComboBox<String> cbUsuarios;
    private JComboBox<String> cbLixeiras;
    private JComboBox<String> cbResiduos;
    private JTextField txtPeso;
    private JTextArea txtLog;
    private JLabel lblMoedas;
    
    public SistemaReciclagemGUI() {
        gerenciadorUsuarios = new GerenciadorUsuarios();
        try {
            usuarios = gerenciadorUsuarios.carregarUsuarios();
        } catch (SistemaArquivosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Falha ao iniciar sistema", e);
        }
        
        lixeiras = new HashMap<>();
        inicializarLixeiras();
        initComponents();
    }
    
    private void inicializarLixeiras() {
        String[] tipos = {"PLÁSTICO", "VIDRO", "METAL", "PAPEL"};
        for (String tipo : tipos) {
            LixeiraInteligente lixeira = new LixeiraInteligente(tipo, 50.0);
            lixeira.addListener(this);
            lixeiras.put(tipo, lixeira);
        }
    }
    
    private void initComponents() {
    setTitle("Sistema de Reciclagem EcoReward");
    setSize(600, 400);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));

    // Painel de controle agora em 1 coluna
    JPanel painelControle = new JPanel(new GridLayout(0, 1, 10, 10));
    
    // --- Linha: Seleção de usuário + botão Novo Usuário ---
    JPanel linhaUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaUsuario.add(new JLabel("Usuário:"));
    cbUsuarios = new JComboBox<>();
    atualizarUsuariosComboBox();
    linhaUsuario.add(cbUsuarios);
    JButton btnNovoUsuario = new JButton("Novo Usuário");
    btnNovoUsuario.addActionListener(this::criarNovoUsuario);
    linhaUsuario.add(btnNovoUsuario);
    painelControle.add(linhaUsuario);

    // --- Linha: Moedas ---
    JPanel linhaMoedas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaMoedas.add(new JLabel("Moedas:"));
    lblMoedas = new JLabel("0");
    linhaMoedas.add(lblMoedas);
    painelControle.add(linhaMoedas);

    // --- Linha: Lixeira ---
    JPanel linhaLixeira = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaLixeira.add(new JLabel("Lixeira:"));
    cbLixeiras = new JComboBox<>(new String[]{"PLÁSTICO", "VIDRO", "METAL", "PAPEL"});
    linhaLixeira.add(cbLixeiras);
    painelControle.add(linhaLixeira);

    // --- Linha: Tipo de Resíduo ---
    JPanel linhaTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaTipo.add(new JLabel("Tipo de Resíduo:"));
    cbResiduos = new JComboBox<>(new String[]{"PLÁSTICO", "VIDRO", "METAL", "PAPEL"});
    linhaTipo.add(cbResiduos);
    painelControle.add(linhaTipo);

    // --- Linha: Peso ---
    JPanel linhaPeso = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaPeso.add(new JLabel("Peso (kg):"));
    txtPeso = new JTextField("0.5", 10);
    linhaPeso.add(txtPeso);
    painelControle.add(linhaPeso);

    // --- Linha: Botão Descarte ---
    JPanel linhaBotao = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    JButton btnDescartar = new JButton("Descartar Resíduo");
    btnDescartar.addActionListener(this::descartarResiduo);
    linhaBotao.add(btnDescartar);
    painelControle.add(linhaBotao);

    add(painelControle, BorderLayout.NORTH);

    // Área de log
    txtLog = new JTextArea();
    txtLog.setEditable(false);
    add(new JScrollPane(txtLog), BorderLayout.CENTER);

    // Atualizar dados do usuário selecionado
    cbUsuarios.addActionListener(e -> atualizarUsuarioSelecionado());
}
    
    private void atualizarUsuariosComboBox() {
        cbUsuarios.removeAllItems();
        for (Usuario usuario : usuarios) {
            cbUsuarios.addItem(usuario.getNome() + " (" + usuario.getId() + ")");
        }
        if (!usuarios.isEmpty()) {
            usuarioAtual = usuarios.get(0);
            atualizarMoedas();
        }
    }
    
    private void atualizarUsuarioSelecionado() {
        int index = cbUsuarios.getSelectedIndex();
        if (index >= 0 && index < usuarios.size()) {
            usuarioAtual = usuarios.get(index);
            atualizarMoedas();
        }
    }
    
    private void atualizarMoedas() {
        lblMoedas.setText(String.valueOf(usuarioAtual.getMoedas()));
    }
    
    private void criarNovoUsuario(ActionEvent e) {
        String nome = JOptionPane.showInputDialog(this, "Digite o nome do novo usuário:");
        if (nome != null && !nome.trim().isEmpty()) {
            String id = "USER" + (usuarios.size() + 1);
            Usuario novoUsuario = new Usuario(id, nome);
            usuarios.add(novoUsuario);
            
            try {
                gerenciadorUsuarios.salvarUsuarios(usuarios);
                atualizarUsuariosComboBox();
                cbUsuarios.setSelectedItem(novoUsuario.getNome() + " (" + novoUsuario.getId() + ")");
                txtLog.append("Novo usuário criado: " + nome + "\n");
            } catch (SistemaArquivosException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void descartarResiduo(ActionEvent e) {
        if (usuarioAtual == null) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário primeiro!");
            return;
        }
        
        try {
            String tipoLixeira = (String) cbLixeiras.getSelectedItem();
            String tipoResiduo = (String) cbResiduos.getSelectedItem();
            double peso = Double.parseDouble(txtPeso.getText());
            
            Residuo residuo = new Residuo(tipoResiduo, peso);
            LixeiraInteligente lixeira = lixeiras.get(tipoLixeira);
            
            lixeira.descartarResiduo(residuo, usuarioAtual);
            txtLog.append("Resíduo descartado: " + tipoResiduo + " (" + peso + " kg)\n");
            
            // Processar recompensa após validação
            residuo.processarRecompensa(usuarioAtual);
            atualizarMoedas();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Peso inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (LixeiraException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no Descarte", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void onResiduoDescartado(Residuo residuo) {
        txtLog.append("Lixeira notificada: " + residuo.getTipo() + " recebido\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SistemaReciclagemGUI().setVisible(true);
        });
    }
}
