package com.multipolar.uiteller.backend.service.sms;

import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

@Service
public class DESedeEncryption {
	private static final String UNICODE_FORMAT = "ASCII";
	private static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	private static final String TRIPLE_DES_TRANSFORMATION = "DESede/ECB/Nopadding";
    private static String BOUNCY_CASTLE_PROVIDER = "BC";
    
    private static Cipher encrypter;
    private static Cipher decrypter;
 
    public DESedeEncryption() throws Exception {
    	String key = "eM0b1l3IndSuMuT12342004+";
        Security.addProvider(new BouncyCastleProvider());
        SecretKey keySpec = new SecretKeySpec(key.getBytes(), DESEDE_ENCRYPTION_SCHEME);
        encrypter = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION, BOUNCY_CASTLE_PROVIDER);
        encrypter.init(Cipher.ENCRYPT_MODE, keySpec);
        decrypter = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION, BOUNCY_CASTLE_PROVIDER);
        decrypter.init(Cipher.DECRYPT_MODE, keySpec);
    }

    public String encode(String unencryptedString) {
    	String encryptedString = null;
		try {
			byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
			if(plainText.length % 8 != 0){ //not a multiple of 8
		        //create a new array with a size which is a multiple of 8
		        byte[] padded = new byte[plainText.length + 8 - (plainText.length % 8)];

		        //copy the old array into it
		        System.arraycopy(plainText, 0, padded, 0, plainText.length);
		        plainText = padded;
		    }
			byte[] encryptedText = encrypter.doFinal(plainText);
			encryptedString = Hex.encodeHexString(encryptedText, false);
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return encryptedString;
    }

    public String decode(String encryptedString) throws IllegalBlockSizeException, BadPaddingException {
    	String decryptedText=null;
    	try {
	    	byte[] encryptedText = Hex.decodeHex(encryptedString);
	        byte[] plainText = decrypter.doFinal(encryptedText);
	        decryptedText= bytes2String(plainText);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	return decryptedText;
    }
    
    
    /**
     * Returns String From An Array Of Bytes
     */
    private static String bytes2String(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }
 
    /**
     * Testing The DESede Encryption And Decryption Technique
     */
    public static void main(String args []) throws Exception
    {
        DESedeEncryption myEncryptor= new DESedeEncryption();
 
        String stringToEncrypt="Speedy via Teller Bank Sumut-b1234 No :061-xxxxxxx Rp.x.xxx.xxx,00 bulan 03/2018 di Cbg Utama Medan via Rek 100.02.04.xxxxxx.x Sukses tgl 15/03/2018 17:00:00";
        String stringToDecrypt="2C528F1200205CF2FD460E8A6F8E869CF84BE3E903769271098E9AD92A324BCE16FBD4BD46F8B23B383743ED3D2CA27D43D503E59F2A0E70AB33B35803BBE1DFCCECCDE6314D2227552C2F784745F463C16C1FDEB9A5F363A0415FA6E5AF4EE9550A56F46C7C373CF6E89100224D00D2C2200BBB2DB65406D0DEDD065E438AC8E3857D8BC61F430FFFF375204D2387D33FCC0BD40A6804E5ABFD1755F8A0A335";
 
        System.out.println("Encrypted Value :" + myEncryptor.encode(stringToEncrypt));
        System.out.println("Decrypted Value :"+myEncryptor.decode(stringToDecrypt));
 
    }
}
