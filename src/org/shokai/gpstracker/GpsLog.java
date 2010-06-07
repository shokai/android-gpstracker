package org.shokai.gpstracker;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import android.app.AlertDialog;
import android.os.Environment;
import android.util.Log;


public class GpsLog {
    
    private List<LogPoint> points;
    private GpsTracker context;
    private File dataDir;
    private int r, g, b;
    
    public GpsLog(GpsTracker context){
        this.context = context;
        this.r = 255;
        this.g = 0;
        this.b = 0;
        this.points = new ArrayList<LogPoint>();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dataDir = new File(Environment.getExternalStorageDirectory(), context.getPackageName()+"/log");
            dataDir.mkdirs();
            context.trace("data dir : "+ dataDir.getPath());
        }
        else{
            context.trace("SD Card not Found");
            new AlertDialog.Builder(context).setMessage("this app uses SD Card").setPositiveButton("OK", null).show();
        }
        try{
            this.loadLog(); 
        }
        catch(Exception e){
            Log.e("GpsTracker", e.getMessage());
        }
    }
    
    public List<LogPoint> getPoints(){
        return this.points;
    }
    
    public int size(){
        return this.points.size();
    }
    
    public LogPoint getPoint(int index){
        return this.points.get(index);
    }
    
    public void add(double lat, double lon){
        LogPoint p = new LogPoint((int) (lat * 1E6), (int) (lon * 1E6));
        p.setR(r);
        p.setG(g);
        p.setB(b);
        this.points.add(p);
        context.trace("add points : " + p.toLog());
        try{
            saveLog(p);
        }
        catch(Exception e){
            Log.e("GpsTracker", e.getMessage());
        }
    }
    
    public LogPoint lastPoint(){
        return this.points.get(this.points.size()-1);
    }
    
    private void saveLog(LogPoint p) throws Exception{
        if (dataDir == null) return;
        File f = new File(this.dataDir, p.getYear()+"-"+p.getMonth()+"-"+p.getDay());

        try{
            OutputStream os = new FileOutputStream(f, true); // append mode
            os.write(p.toLog().getBytes());
            os.close();
        }
        catch(Exception e){
            throw e;
        }
        context.trace("saveLog : " + f.getPath());
    }
    
    public boolean loadLog() throws Exception{
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        try{
            return this.loadLog(year, month, day);
        }
        catch(Exception e){
            throw e;
        }
    }
    
    public boolean loadLog(String fileName) throws Exception{
        try{
            File f = new File(this.dataDir, fileName);
            return this.loadLog(f);
        }
        catch(Exception e){
            throw e;
        }
    }
    
    public boolean loadLog(int year, int month, int day) throws Exception{
        File f = new File(this.dataDir, year+"-"+month+"-"+day);
        try{
            return this.loadLog(f);
        }
        catch(Exception e){
            throw e;
        }
    }
    
    public boolean loadLog(File f) throws Exception{
        if(!f.exists()) return false;
        context.trace("load Log - " + f.getPath());
        try{
            InputStream is = new FileInputStream(f);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            String[] lines = new String(bytes).split("\n");
            List<LogPoint> points = new ArrayList<LogPoint>();
            for(String line : lines){
                LogPoint p = LogPoint.parse(line); 
                points.add(p);
                context.trace(p.toLog());
            }
            if (points.size() > 0){
                this.points = points;
                return true;
            }
            return false;
        }
        catch(Exception e){
            throw e;
        }
    }

    public File getDataDir(){
        return this.dataDir;
    }
    
    public String[] fileNames(){
        ArrayList<String> files = new ArrayList<String>();
        for(String file : this.dataDir.list()){
            files.add(file);
        }
        
        Collections.sort(files, new FileNamesComparator());
        Collections.reverse(files);
        
        String[] result = new String[files.size()];
        for(int i = 0; i < files.size(); i++){
            result[i] = files.get(i);
        }
        
        return result;
    }
    
    public class FileNamesComparator implements Comparator {
        Pattern pattern_ymd;

        public FileNamesComparator() {
            this.pattern_ymd = Pattern.compile("^([1-9][0-9]*)-([1-9][0-9]*)-([1-9][0-9]*)$");
        }

        public int compare(Object obj_a, Object obj_b) {
            if (!pattern_ymd.matcher(obj_a.toString()).matches()) return 1;
            else if (!pattern_ymd.matcher(obj_b.toString()).matches()) return -1;
            String[] tmp_a = obj_a.toString().split("-");
            String[] tmp_b = obj_b.toString().split("-");
            int[] a = new int[3];
            int[] b = new int[3];
            for (int i = 0; i < 3; i++) {
                a[i] = Integer.parseInt(tmp_a[i]);
                b[i] = Integer.parseInt(tmp_b[i]);
            }
            if (a[0] > b[0]) return 1;
            if (a[0] < b[0]) return -1;
            if (a[1] > b[1]) return 1;
            if (a[1] < b[1]) return -1;
            if (a[2] > b[2]) return 1;
            if (a[2] < b[2]) return -1;
            return 0;
        }
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
    
}

