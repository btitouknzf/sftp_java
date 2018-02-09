package jp.co.nttdata.shinkin.sftp.SFTP_transfer;

import java.io.IOException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

/**
 * Hello world!
 */
public class App {
    public static void main(
            String[] args) throws JSchException, SftpException, IOException {

        final SftpSample sftp = new SftpSample();
        sftp.putFile("10.9.52.127",
                22,
                "Jiki-Admin",
                "Jiki-Admin",
                "C:/Program Files/WinSCP/key/id_rsa.ppk",
                "C:/gitbucket/IB2020_group/SFTP_JSch/spring.jpg",
                "C:/work/spring.jpg");
        
        sftp.getFIle("10.9.52.127", 22, "Jiki-Admin", "Jiki-Admin",
                "C:/Program Files/WinSCP/key/id_rsa.ppk",
                "C:/gitbucket/IB2020_group/SFTP_JSch/fall.jpg",
                "C:/work/fall.jpg");

    }
}
