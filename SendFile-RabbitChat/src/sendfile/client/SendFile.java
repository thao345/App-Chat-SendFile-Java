package sendfile.client;


import sendfile.client.MainForm;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hThao
 */

public class SendFile extends javax.swing.JFrame {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String myusername;
    private String host;
    private int port;
    private StringTokenizer st;
    private String sendTo;
    private String file;
    private MainForm main;
    private Object chooser;
    
    
     // Tạo một form SendFile
     
    public SendFile() {
        initComponents();
        MyInit();
        progressbar.setVisible(false);
    }
    void MyInit(){
         setLocationRelativeTo(null);
     }
    /* Phương thức này được sử dụng để thiết lập kết nối đến một máy chủ thông qua  socket và chuẩn bị các thông tin cần thiết để chia sẻ tập tin với máy chủ đó.

Tiếp theo, phương thức tạo một đối tượng Socket và DataOutputStream và DataInputStream được sử dụng để ghi và đọc dữ liệu từ socket. 
    Sau đó, phương thức gửi một chuỗi thông điệp "CMD_SHARINGSOCKET [sender]" tới máy chủ thông qua socket sử dụng phương thức writeUTF() của DataOutputStream. 
    Chuỗi này được định dạng theo một định dạng cụ thể.

Sau đó, một luồng mới được tạo ra bằng cách sử dụng lớp SendFileThread và được khởi chạy bằng phương thức start() để bắt đầu chia sẻ tập tin với máy chủ.

Cuối cùng, phương thức trả về true nếu kết nối được thiết lập thành công và false nếu có lỗi xảy ra trong quá trình thiết lập kết nối.*/
    public boolean prepare(String u, String h, int p, MainForm m){
        this.host = h;
        this.myusername = u;
        this.port = p;
        this.main = m;
        try {
            socket = new Socket(host, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            //  Format: CMD_SHARINGSOCKET [sender]
            String format = "CMD_SHARINGSOCKET "+ myusername;
            dos.writeUTF(format);
            System.out.println(format);
            
            new Thread(new SendFileThread(this)).start();//chia sẻ tập tin với máy chủ.
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    
    
    class SendFileThread implements Runnable{
        private SendFile form;
        public SendFileThread(SendFile form){
            this.form = form;
        }
        
        private void closeMe(){
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("[closeMe]: "+e.getMessage());
            }
            dispose();
        }
        
        @Override
        public void run() {
            try {
                while(!Thread.currentThread().isInterrupted()){
                    String data = dis.readUTF();  // Đọc nội dung của dữ liệu được nhận từ Server
                    st = new StringTokenizer(data);
                    String cmd = st.nextToken();  //  Lấy chữ đầu tiên từ dữ liệu
                    switch(cmd){
                        case "CMD_RECEIVE_FILE_ERROR":  // Định dạng: CMD_RECEIVE_FILE_ERROR [Message]
                            String msg = "";
                            while(st.hasMoreTokens()){
                                msg = msg+" "+st.nextToken();
                            }
                            form.updateAttachment(false);
                            JOptionPane.showMessageDialog(SendFile.this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
                            this.closeMe();
                            break;
                            
                        case "CMD_RECEIVE_FILE_ACCEPT":  // Định dạng: CMD_RECEIVE_FILE_ACCEPT [Message]
                            /*  Bắt đầu khởi động thread File đính kèm   */

                            new Thread(new SendingFileThread(socket, file, sendTo, myusername, SendFile.this)).start();
                            break;
                            
                        case "CMD_SENDFILEERROR":
                            String emsg = "";
                            while(st.hasMoreTokens()){
                                emsg = emsg +" "+ st.nextToken();
                            }                                                     
                            System.out.println(emsg);                            
                            JOptionPane.showMessageDialog(SendFile.this, emsg,"Lỗi", JOptionPane.ERROR_MESSAGE);
                            form.updateAttachment(false);
                            form.disableGUI(false);
                            form.updateBtn("Gửi File");
                            break;
                        
                        
                        case "CMD_SENDFILERESPONSE":
                            /*
                            Format: CMD_SENDFILERESPONSE [username] [Message]
                            */
                            String rReceiver = st.nextToken();
                            String rMsg = "";
                            while(st.hasMoreTokens()){
                                rMsg = rMsg+" "+st.nextToken();
                            }
                            form.updateAttachment(false);
                            JOptionPane.showMessageDialog(SendFile.this, rMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);
                            dispose();
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtFile = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtSendTo = new javax.swing.JTextField();
        progressbar = new javax.swing.JProgressBar();
        btnSendFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dịch vụ Gửi File - RabbitChat");

        jLabel1.setText("Chọn File :");

        txtFile.setEditable(false);
        txtFile.setBackground(new java.awt.Color(255, 255, 255));
        txtFile.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFileActionPerformed(evt);
            }
        });

        btnBrowse.setBackground(new java.awt.Color(255, 153, 153));
        btnBrowse.setForeground(new java.awt.Color(0, 0, 0));
        btnBrowse.setText("Tìm File");
        btnBrowse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        jLabel2.setText("Gửi đến :");

        progressbar.setStringPainted(true);

        btnSendFile.setBackground(new java.awt.Color(255, 153, 153));
        btnSendFile.setForeground(new java.awt.Color(0, 0, 0));
        btnSendFile.setText("Gửi File");
        btnSendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(progressbar, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btnSendFile))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnBrowse))
                                .addComponent(txtSendTo, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addContainerGap(22, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBrowse)
                    .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSendTo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSendFile, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressbar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFileActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        // TODO add your handling code here:
        showOpenDialog();
    }//GEN-LAST:event_btnBrowseActionPerformed
/*Trong phương thức btnSendFileActionPerformed, khi người dùng nhấn nút "Gửi file", đầu tiên phương thức sẽ lấy giá trị của các trường nhập liệu txtSendTo và
  txtFile. Nếu cả hai trường đều có giá trị, phương thức sẽ gửi một chuỗi định dạng thông điệp có chứa tên file và thông tin người gửi, người nhận đến server 
    thông qua đối tượng DataOutputStream dos.

Chuỗi định dạng thông điệp này được đặt tên là format và có dạng: "CMD_SEND_FILE_XD [sender] [receiver] [filename]". Cụ thể, chuỗi này sẽ được gửi đến server 
    thông qua phương thức writeUTF() của đối tượng dos.

Sau khi gửi thông điệp, nút "Gửi file" sẽ bị vô hiệu hóa (setEnabled(false)) và giao diện sẽ hiển thị thông báo "Đang gửi đi..." để người dùng biết rằng quá 
    trình gửi file đang được thực hiện.*/
    private void btnSendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendFileActionPerformed
        // TODO add your handling code here:
        sendTo = txtSendTo.getText(); //ten ng gửi
        file = txtFile.getText(); // đường dãn file

        if((sendTo.length() > 0) && (file.length() > 0)){
            try {
                // Format: CMD_SEND_FILE_XD [sender] [receiver] [filename]
                txtFile.setText("");
                String fname = getThisFilename(file);
                String format = "CMD_SEND_FILE_XD "+myusername+" "+sendTo+" "+fname;
                dos.writeUTF(format);
                System.out.println(format);
                updateBtn("Đang gửi đi...");
                btnSendFile.setEnabled(false); // khi đang gửi đi nút gửi file bị vô hiệu hoá
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }else{
            JOptionPane.showMessageDialog(this, "Không để trống.!","Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSendFileActionPerformed
    /*Phương thức "showOpenDialog()" trong đoạn mã của bạn dùng để hiển thị hộp thoại "Mở tập tin" (open file dialog) của hệ thống. Sau khi người dùng chọn
    một tập tin cụ thể, phương thức sẽ trả về một giá trị nguyên để chỉ ra liệu người dùng đã chọn tập tin và nhấn nút "Mở" hay không.

Cụ thể, đoạn mã của bạn tạo ra một đối tượng JFileChooser (hộp thoại chọn tập tin) và gọi phương thức "showOpenDialog" trên đối tượng này. Khi phương thức
    này được gọi, hộp thoại sẽ được hiển thị và cho phép người dùng chọn một tập tin. Nếu người dùng chọn tập tin và nhấn nút "Mở", phương thức "showOpenDialog" 
    sẽ trả về giá trị "APPROVE_OPTION" (được định nghĩa trong lớp JFileChooser), ngược lại nếu người dùng nhấn nút "Hủy", phương thức sẽ trả về
    giá trị "CANCEL_OPTION".

Trong trường hợp người dùng đã chọn một tập tin và nhấn nút "Mở", phương thức sẽ đặt giá trị của đường dẫn tập tin được chọn vào thành phần văn bản "txtFile" 
    để hiển thị đường dẫn của tập tin đó trên giao diện. Nếu người dùng nhấn nút "Hủy", phương thức sẽ xóa bỏ nội dung trong thành phần văn bản "txtFile".*/
    public void showOpenDialog(){
        JFileChooser chooser = new JFileChooser();
        int intval = chooser.showOpenDialog(this);
        if(intval == chooser.APPROVE_OPTION){// Nếu người dùng đã chọn tập tin
            txtFile.setText(chooser.getSelectedFile().toString());// Lấy danh sách các tập tin được chọn
        }else{
            txtFile.setText("");
        }
    }
    
    
    /*   Phương thức này sẽ disable/enabled tất cả GUI   */
    public void disableGUI(boolean d){
        if(d){ // Disable
            txtSendTo.setEditable(false);
            btnBrowse.setEnabled(false);
            btnSendFile.setEnabled(false);
            txtFile.setEditable(false);
            progressbar.setVisible(true);
        } else { // Enable
            txtSendTo.setEditable(true);
            btnSendFile.setEnabled(true);
            btnBrowse.setEnabled(true);
            txtFile.setEditable(true);
            progressbar.setVisible(false);
        }
    }
    
    
    /*  Nhận Form Title   */
    public void setMyTitle(String s){
        setTitle(s);
    }
    
    /*   Hàm này sẽ đóng Form  */
    protected void closeThis(){
        dispose();
    }
    
    /*  Hàm này sẽ nhận Filename */
    public String getThisFilename(String path){
        File p = new File(path);
        String fname = p.getName();
        return fname.replace(" ", "_");
    }
    
    /*  Cập nhật file đính kèm   */
    public void updateAttachment(boolean b){
        main.updateAttachment(b);
    }
    
    /*  Cập nhật nút btnSendFile text  */
    public void updateBtn(String str){
        btnSendFile.setText(str);
    }
    
    /**
     * Update progress bar
     * @param val 
     */
    public void updateProgress(int val){
        progressbar.setValue(val);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SendFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SendFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SendFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SendFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SendFile().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnSendFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JProgressBar progressbar;
    private javax.swing.JTextField txtFile;
    private javax.swing.JTextField txtSendTo;
    // End of variables declaration//GEN-END:variables
}
