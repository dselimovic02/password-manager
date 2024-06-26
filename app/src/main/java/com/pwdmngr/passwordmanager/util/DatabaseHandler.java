package com.pwdmngr.passwordmanager.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pwdmngr.passwordmanager.model.PwdModel;
import com.pwdmngr.passwordmanager.modules.AESAlgorithm;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "passwordsDatabase";
    private static final String PWD_TABLE = "pwds";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String USERNAME = "username";
    private static final String PWD = "pwd";
    private static final String URL = "url";
    private static final String CREATE_TABLE = String.format("" +
            "CREATE TABLE " + PWD_TABLE +
            "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TITLE + " TEXT," +
            USERNAME + " TEXT," +
            PWD + " TEXT," +
            URL + " TEXT);");
    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PWD_TABLE);
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertPassword(PwdModel pwd) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnrecoverableEntryException, IllegalBlockSizeException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, BadPaddingException, InvalidKeyException, NoSuchProviderException {
        ContentValues cv = new ContentValues();
        cv.put(TITLE, pwd.getTitle());
        cv.put(USERNAME, pwd.getUsername());
        cv.put(PWD, AESAlgorithm.encrypt(pwd.getPwd()));
        cv.put(URL, pwd.getUrl());
        db.insert(PWD_TABLE, null, cv);
    }

    public List<PwdModel> getAllPasswords() {
        List<PwdModel> pwdList = new ArrayList<>();
        Cursor cur = null;
        try {
            cur = db.query(PWD_TABLE, null, null, null, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    PwdModel pwd = makePwdModel(cur);
                    pwdList.add(pwd);
                } while (cur.moveToNext());
            }
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return pwdList;
    }

    public PwdModel getPassword(int id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnrecoverableEntryException, IllegalBlockSizeException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, BadPaddingException, InvalidKeyException, NoSuchProviderException {
        PwdModel pwd = null;
        Cursor cur = null;
        String where = String.format("%s = %d", ID, id);
        cur = db.query(PWD_TABLE, null, where, null, null, null, null);

        if(cur != null && cur.moveToNext()) {
            pwd = makePwdModel(cur);
            cur.close();
        }

        return pwd;
    }

    private PwdModel makePwdModel(Cursor cur) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnrecoverableEntryException, IllegalBlockSizeException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, BadPaddingException, InvalidKeyException, NoSuchProviderException {

        PwdModel pwd = new PwdModel();
        pwd.setId(cur.getInt(cur.getColumnIndexOrThrow(ID)));
        pwd.setTitle(cur.getString(cur.getColumnIndexOrThrow(TITLE)));
        pwd.setUsername(cur.getString(cur.getColumnIndexOrThrow(USERNAME)));
        pwd.setPwd(AESAlgorithm.decrypt(cur.getString(cur.getColumnIndexOrThrow(PWD))));
        pwd.setUrl(cur.getString(cur.getColumnIndexOrThrow(URL)));

        return pwd;
    }

    public void update(int id, String title, String username, String pwd, String url) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnrecoverableEntryException, IllegalBlockSizeException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, BadPaddingException, InvalidKeyException, NoSuchProviderException {
        ContentValues cv = new ContentValues();

        if(title != null) cv.put(TITLE, title);
        if(username != null) cv.put(USERNAME, username);
        if(pwd != null) cv.put(PWD, AESAlgorithm.encrypt(pwd));
        if(url != null) cv.put(URL, url);


        if (cv.size() > 0)
            db.update(PWD_TABLE, cv, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void deletePwd(int id) {
        db.delete(PWD_TABLE, ID + "=?", new String[] {String.valueOf(id)});
    }
}
