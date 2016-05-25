package org.android.util;

import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

//import java.sql.ResultSetMetaData;

/**
 * Created by joe on 16/5/18.
 */
public class Mysql {
    private Connection m_fConn;
    //private Statement m_fQuery;
    private int  m_nQueryNum;
    private String m_strConnect;
    private String m_strHost;
    private String m_strPwd;
    private String m_strUser;
    

    public Mysql(String host,int port,String user,String pass,String dbname,String charset)
    {
        System.out.println("Database()");
        m_nQueryNum=0;
        if(host.isEmpty())
        {
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            m_strHost=host;
            m_strUser=user;
            m_strPwd=pass;
            m_strConnect="jdbc:mysql://"+host+":"+port+"/"+dbname;

            while(!connect())
            {
                LogUtil.e("cannot connect host %s,try again!", m_strHost);
            }

            //m_fQuery=m_fConn.createStatement();
            query("SET character_set_connection="+charset+", character_set_results="+charset+", character_set_client=binary");
            //m_fQuery.execute("SET character_set_connection="+charset+", character_set_results="+charset+", character_set_client=binary");
            //m_fQuery.close();
        }
        catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("connect to db Exception,host %s db %s exit!",m_strHost,dbname);
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public boolean connect()
    {
        try {
            LogUtil.d("begin to connect db %s", m_strHost);
            Class.forName("com.mysql.jdbc.Driver");
            m_fConn = DriverManager.getConnection(m_strConnect,m_strUser,m_strPwd);
            if(m_fConn.isClosed())
            {
                LogUtil.e("DriverManager.getConnection failed");
                return false;
            }
            LogUtil.d("connect ok");
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("connect to db Exception");
            //e.printStackTrace();
            return false;
        }
    }

    synchronized public ArrayList<HashMap<String,Object>> getNames(String fmt, Object...args) {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return null;
            }

            ArrayList<HashMap<String,Object>> resArrayList=new ArrayList<HashMap<String,Object>>();
            Statement fQuery=m_fConn.createStatement();
            ResultSet rSet=fQuery.executeQuery(sqlString);

            m_nQueryNum++;
            ResultSetMetaData metaData=(ResultSetMetaData) rSet.getMetaData();
            int num=metaData.getColumnCount();

            while (rSet.next()) {
                HashMap<String,Object> resMap=new HashMap<String,Object>();
                for(int i=0;i<num;i++)
                    resMap.put(metaData.getColumnName(i+1),rSet.getObject(i+1));

                resArrayList.add(resMap);
            }
            fQuery.close();
            return resArrayList;

        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return getNames(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            LogUtil.e("MySQLNonTransientConnectionException sql is "+sqlString);
        }
        catch (Exception e) {
            LogUtil.e("Exception sql is "+sqlString);
            //e.printStackTrace();
        }
        return null;
    }

    synchronized public int getCount(String tableName,String whereStr){
        return getInt("select count(*) from %s where %s", tableName,whereStr);
    }

    //返回以列名的值为索引的hashmap,如
    //id1=>(valName1=>value1,...,valNameN=>valueN)
    //id2=>(valName1=>value1,...,valNameN=>valueN)
    //所以列名的值，必须为整数类型
    synchronized public HashMap<Integer,HashMap<String,Object>> getNamesWithKey(String colName,String fmt,Object...args) {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return null;
            }

            HashMap<Integer,HashMap<String,Object>> resHash=new HashMap<Integer,HashMap<String,Object>>();
            Statement  fQuery=m_fConn.createStatement();
            ResultSet rSet=fQuery.executeQuery(sqlString);

            m_nQueryNum++;
            ResultSetMetaData metaData=(ResultSetMetaData) rSet.getMetaData();
            int num=metaData.getColumnCount();

            while (rSet.next()) {
                HashMap<String,Object> resMap=new HashMap<String,Object>();
                int key=0;
                for(int i=0;i<num;i++)
                {
                    if( colName.compareTo(metaData.getColumnName(i+1))!=0 )
                        resMap.put(metaData.getColumnName(i+1),rSet.getObject(i+1));
                    else
                        key=rSet.getInt(i+1);
                }

                resHash.put(key,resMap);
            }
            fQuery.close();
            return resHash;

        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return getNamesWithKey(colName,sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            LogUtil.e("Exception sql is "+sqlString);
        }
        catch (Exception e) {
            LogUtil.e("Exception sql is "+sqlString);
            e.printStackTrace();
        }
        return null;
    }

    synchronized public HashMap<String,Object> getName(String fmt,Object...args) {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                while(!connect()){}
                return null;
            }

            Statement fQuery=m_fConn.createStatement();
            ResultSet rSet=fQuery.executeQuery(sqlString);

            m_nQueryNum++;
            ResultSetMetaData metaData=(ResultSetMetaData) rSet.getMetaData();
            int num=metaData.getColumnCount();


            HashMap<String,Object> resMap=new HashMap<String,Object>();
            if(rSet.next())
            {

                for(int i=0;i<num;i++)
                    resMap.put(metaData.getColumnName(i+1),rSet.getObject(i+1));
            }

            fQuery.close();

            return resMap;
        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return getName(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            //e.printStackTrace();
            LogUtil.e("MySQLNonTransientConnectionException,sql is "+sqlString);
        }
        catch (Exception e) {
            LogUtil.e("Exception,sql is "+sqlString);
            //e.printStackTrace();
        }
        return null;
    }

    public ArrayList<ArrayList<Object>> getRows(String fmt,Object...args) {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return null;
            }

            ArrayList<ArrayList<Object>> resArrayList=new ArrayList<ArrayList<Object>>();
            Statement fQuery=m_fConn.createStatement();
            ResultSet rSet=fQuery.executeQuery(sqlString);

            m_nQueryNum++;
            ResultSetMetaData metaData=(ResultSetMetaData) rSet.getMetaData();
            int num=metaData.getColumnCount();

            while (rSet.next()) {
                ArrayList<Object> resSubList=new ArrayList<Object>();
                for(int i=0;i<num;i++)
                    resSubList.add(rSet.getObject(i+1));

                resArrayList.add(resSubList);
            }
            fQuery.close();
            return resArrayList;

        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return getRows(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            //e.printStackTrace();
            LogUtil.e("MySQLNonTransientConnectionException sql is "+sqlString);
        }
        catch (Exception e) {
            LogUtil.e("Exception sql is "+sqlString);
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public ArrayList<Integer> getArrayListInt(String fmt,Object...args) {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return null;
            }

            ArrayList<Integer> resArrayList=new ArrayList<Integer>();
            Statement fQuery=m_fConn.createStatement();
            ResultSet rSet=fQuery.executeQuery(sqlString);
            m_nQueryNum++;

            while (rSet.next()) {
                int a=rSet.getInt(1);
                resArrayList.add(a);
            }
            fQuery.close();
            return resArrayList;

        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return getArrayListInt(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            //e.printStackTrace();
            LogUtil.e("MySQLNonTransientConnectionException sql is "+sqlString);
        }
        catch (Exception e) {
            LogUtil.e("Exception sql is "+sqlString);
            //e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Long> getArrayListLong(String fmt,Object...args) {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return null;
            }

            ArrayList<Long> resArrayList=new ArrayList<Long>();
            Statement fQuery=m_fConn.createStatement();
            ResultSet rSet=fQuery.executeQuery(sqlString);
            m_nQueryNum++;

            while (rSet.next()) {
                long a=rSet.getLong(1);
                resArrayList.add(a);
            }
            fQuery.close();
            return resArrayList;

        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return getArrayListLong(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            //e.printStackTrace();
            LogUtil.e("MySQLNonTransientConnectionException sql is "+sqlString);
        }
        catch (Exception e) {
            LogUtil.e("Exception sql is "+sqlString);
            //e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getArrayListString(String fmt,Object...args) {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return null;
            }

            ArrayList<String> resArrayList=new ArrayList<String>();
            Statement fQuery=m_fConn.createStatement();
            ResultSet rSet=fQuery.executeQuery(sqlString);
            m_nQueryNum++;

            while (rSet.next()) {
                resArrayList.add(rSet.getString(1));
            }
            fQuery.close();
            return resArrayList;

        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return getArrayListString(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            //e.printStackTrace();
            LogUtil.e("MySQLNonTransientConnectionException sql is "+sqlString);
        }
        catch (Exception e) {
            LogUtil.e("Exception sql is "+sqlString);
            //e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Object> getRow(String fmt,Object...args) {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                while(!connect()){}
                return null;
            }

            ArrayList<Object> resArrayList=new ArrayList<Object>();
            Statement fQuery=m_fConn.createStatement();
            ResultSet rSet=fQuery.executeQuery(sqlString);

            m_nQueryNum++;
            ResultSetMetaData metaData=(ResultSetMetaData) rSet.getMetaData();
            int num=metaData.getColumnCount();

            if(rSet.next())
            {
                for(int i=0;i<num;i++)
                    resArrayList.add(rSet.getObject(i+1));
            }
            fQuery.close();

            return resArrayList;
        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return getRow(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            //e.printStackTrace();
            LogUtil.e("MySQLNonTransientConnectionException sql is "+sqlString);
        }
        catch (Exception e) {
            LogUtil.e("Exception sql is "+sqlString);
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public int getInt(String fmt,Object...args) {
        String retString=getString(fmt,args);
        if(retString!=null && retString!="")
            return Integer.parseInt(retString);
        else
            return 0;
    }

    public long getLong(String fmt,Object...args) {
        String retString=getString(fmt,args);
        if(retString!=null && retString!="")
            return Long.parseLong(retString);
        else
            return 0;
    }

    public String getString(String fmt,Object...args) {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return null;
            }

            Statement fQuery=m_fConn.createStatement();
            ResultSet rSet=fQuery.executeQuery(sqlString);

            m_nQueryNum++;
            String ret="";
            if(rSet.next())
                ret=rSet.getString(1);
            fQuery.close();

            return ret;
        }

        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return getString(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            LogUtil.e("MySQLNonTransientConnectionException,sql is "+sqlString);
            e.printStackTrace();
        }
        catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("Exception,sql is "+sqlString);
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public boolean query(String fmt,Object...args)
    {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return false;
            }

            Statement fQuery=m_fConn.createStatement();
            fQuery.execute(sqlString);
            fQuery.close();
            m_nQueryNum++;

            return true;
        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return query(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            //e.printStackTrace();
            LogUtil.e("MySQLNonTransientConnectionException,sql is "+sqlString);
        }
        catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("Exception,sql is "+sqlString);
            e.printStackTrace();
            System.exit(-1);
        }
        return false;
    }

    public int affectedRows(String sqlString)
    {
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return 0;
            }
            String lowString=sqlString.toLowerCase();

            if(lowString.startsWith("update") || lowString.startsWith("insert") || lowString.startsWith("delete"))
            {
                Statement fQuery=m_fConn.createStatement();
                int rows= fQuery.executeUpdate(sqlString);

                fQuery.close();
                m_nQueryNum++;

                return rows;
            }
            return 0;
        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return affectedRows(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {

            LogUtil.e("MySQLNonTransientConnectionException,sql is "+sqlString);
        }
        catch (Exception e) {
            LogUtil.e("Exception,sql is "+sqlString);
            //e.printStackTrace();
        }
        return 0;
    }


    public int uid(String fmt,Object...args)
    {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return 0;
            }

            Statement fQuery=m_fConn.createStatement();
            fQuery.executeUpdate(sqlString,Statement.RETURN_GENERATED_KEYS);
            ResultSet res =fQuery.getGeneratedKeys();
            int id=0;
            if(res.next())
                id=res.getInt(1);
            fQuery.close();
            m_nQueryNum++;

            return id;
        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return uid(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            LogUtil.e("MySQLNonTransientConnectionException,sql is "+sqlString);
        }
        catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("Exception,sql is "+sqlString);
            e.printStackTrace();
            System.exit(-1);
        }
        return 0;
    }

    public long uidAsLong(String fmt,Object...args)
    {
        String sqlString=String.format(fmt, args);
        try {
            if(m_fConn.isClosed())
            {
                if(!connect())
                    return 0;
            }

            Statement fQuery=m_fConn.createStatement();
            fQuery.executeUpdate(sqlString,Statement.RETURN_GENERATED_KEYS);
            ResultSet res =fQuery.getGeneratedKeys();
            long id=0;
            if(res.next())
                id=res.getLong(1);
            fQuery.close();
            m_nQueryNum++;

            return id;
        }
        catch(CommunicationsException e)
        {
            //e.printStackTrace();
            LogUtil.e("CommunicationsException found! reconnect!");
            while(!connect()){}
            return uid(sqlString);
        }
        catch(MySQLNonTransientConnectionException e)
        {
            LogUtil.e("MySQLNonTransientConnectionException,sql is "+sqlString);
        }
        catch (Exception e) {
            // TODO: handle exception
            LogUtil.e("Exception,sql is "+sqlString);
            e.printStackTrace();
            System.exit(-1);
        }
        return 0;
    }

    public int getQueryNum(){
        return m_nQueryNum;
    }
}
