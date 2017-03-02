/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeditv2;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
/**
 *
 * @author HajiMalung
 */
public class FXMLDocumentController implements Initializable {
    @FXML
    private MenuBar m_bar;
    @FXML
    private Menu file_menu;
    @FXML
    private Menu edit_menu;
    @FXML
    private Menu build_menu;
    @FXML
    private MenuItem new_option;
    @FXML
    private MenuItem open_option;
    @FXML
    private MenuItem save_option;
    @FXML
    private MenuItem saveas_option;
    @FXML
    private MenuItem close_option;
    @FXML
    private TextArea codearea;
    
    private String filepath;
    private boolean contentchanged;
    private String content;
    private String clipboard;
    int caretpos;
    
    @FXML
    private MenuItem cut_option;
    @FXML
    private MenuItem copy_option;
    @FXML
    private MenuItem paste_option;
    @FXML
    private MenuItem compile_option;
    @FXML
    private MenuItem run_option;
    @FXML
    private TextArea status;
    @FXML
    private MenuItem dispStatus;
    @FXML
    private Label countwords;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        new_option.setAccelerator(new KeyCodeCombination(KeyCode.N,KeyCombination.CONTROL_DOWN));
        open_option.setAccelerator(new KeyCodeCombination(KeyCode.O,KeyCombination.CONTROL_DOWN));
        save_option.setAccelerator(new KeyCodeCombination(KeyCode.S,KeyCombination.CONTROL_DOWN));
        saveas_option.setAccelerator(new KeyCodeCombination(KeyCode.S,KeyCombination.ALT_DOWN));
        close_option.setAccelerator(new KeyCodeCombination(KeyCode.Q,KeyCombination.CONTROL_DOWN));
        
        cut_option.setAccelerator(new KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_DOWN));
        copy_option.setAccelerator(new KeyCodeCombination(KeyCode.C,KeyCombination.CONTROL_DOWN));
        paste_option.setAccelerator(new KeyCodeCombination(KeyCode.V,KeyCombination.CONTROL_DOWN));
        
        compile_option.setAccelerator(new KeyCodeCombination(KeyCode.F9,KeyCombination.CONTROL_DOWN));
        run_option.setAccelerator(new KeyCodeCombination(KeyCode.F5,KeyCombination.ALT_DOWN));
        
        
        filepath=null;
        content=null;
        contentchanged=false;
        clipboard=null;
        caretpos=0;
        
        status.getStyleClass().add("text-area1");
        
        status.setText("JEDIT LOADED SUCCESSFULLY");
        codearea.setText("class Untitled{\n\tpublic static void main(String args[]){\n\t\t//insert your code here\n\t}\n}");
    }    
    @FXML
    private void newClicked(ActionEvent event) {
        filepath=null;
        codearea.setText("class Untitled{\n\tpublic static void main(String args[]){\n\t\t//insert your code here\n\t}\n}");
        content=null;
        contentchanged=false;
        status.setText("New Page Loaded");
    }

    @FXML
    private void openClicked(ActionEvent event) {
        
        FileChooser fileselect=new FileChooser();
        fileselect.setTitle("Open File");
        File file=fileselect.showOpenDialog(null);
        if (file!=null) {
            filepath=file.getPath();
            content=readFile(file);
            codearea.setText(content);
            status.setText("File Opened Successfully");
            contentchanged=false;
            countLinesWords(content);
        }else{ 
            status.setText("Status: You Didn\'t selected any file");
        }
        
    }
    private String readFile(File file){
    String text="";
            try{
                FileInputStream fis=new FileInputStream(file);
                int i=fis.read();
                while(i!=-1)
                {
                    text+=(char)i;
                    i=fis.read();
                }
            }catch(Exception e){
                status.setText("Status: File Reading Failed");
            }
        
        return text;
    }

    @FXML
    private void closeClicked(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void compileClicked(ActionEvent event) {
        if(filepath==null && "".equals(codearea.getText()))
        {
            status.setText("There is no code in code area to compile..");
            return;
        }else if(filepath==null && !"".equals(codearea.getText())){
            saveClicked(event);
        }
        if(contentchanged)
        {
            status.setText("saving Document.....");
            File file=new File(filepath);
            saveFile(file);
        }
        
        try{
            status.setText("compiling....");
            ProcessBuilder pb=new ProcessBuilder("javac",filepath);
            Process p=pb.start();
            BufferedReader rd=new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder bl=new StringBuilder();
            String ln="";
            while ( (ln = rd.readLine()) != null ) {
               bl.append(ln);
               bl.append(System .getProperty( "line.separator" )); 
                }
            String errors = bl.toString();
            if(errors.length()==0)
            {
                status.setText(status.getText()+"\n There are no errors in code");
                
                String text="@echo off\n" +
                            "color 0A\n"+
                            "setlocal\n" +
                            "set myString=%0\n" +
                            "call :strlen result myString\n" +
                            "\n" +
                            "\n" +
                            "setlocal enabledelayedexpansion\n" +
                            "set S= %myString%\n" +
                            "set I=0\n" +
                            "set L=-1\n" +
                            ":l\n" +
                            "if \"!S:~%I%,1!\"==\"\" goto ld\n" +
                            "if \"!S:~%I%,1!\"==\"\\\" set L=%I%\n" +
                            "set /a I+=1\n" +
                            "goto l\n" +
                            ":ld\n" +
                            "\n" +
                            "set /a sub=%result%-%L%\n" +
                            "\n" +
                            "set /a L+=1\n" +
                            "set /a sub-=5\n" +
                            "\n" +
                            "java \"!S:~%L%,%sub%!\"\n" +
                            "\n\n" +
                            "pause\n" +
                            "goto :eof\n" +
                            ":strlen <resultVar> <stringVar>\n" +
                            "(   \n" +
                            "    setlocal EnableDelayedExpansion\n" +
                            "    set \"s=!%~2!#\"\n" +
                            "    set \"len=0\"\n" +
                            "    for %%P in (4096 2048 1024 512 256 128 64 32 16 8 4 2 1) do (\n" +
                            "        if \"!s:~%%P,1!\" NEQ \"\" ( \n" +
                            "            set /a \"len+=%%P\"\n" +
                            "            set \"s=!s:~%%P!\"\n" +
                            "        )\n" +
                            "    )\n" +
                            ")\n" +
                            "( \n" +
                            "    endlocal\n" +
                            "    set \"%~1=%len%\"\n" +
                            "    exit /b\n" +
                            ")";
                    try{
                        BufferedWriter out=new BufferedWriter(new FileWriter(filepath.substring(0,filepath.length()-5)+".bat"));
                        out.write(text);
			out.close();
                        status.setText(status.getText()+"\nClass and Executable files created Successfully.");
                    }catch(Exception e){
                        status.setText(status.getText()+"\n Executable file creation Failed.");
                    }
            }else{
                status.setText(status.getText()+"\n"+errors);
            }
        }catch(Exception e)
        {
        }
    }
    @FXML
    private void runClicked(ActionEvent event) {
        if (filepath==null && "".equals(codearea.getText())) {
            status.setText("There is no code in code area to run");
            return;
        }
        try{
            status.setText("Running.......");
            Runtime rt=Runtime.getRuntime();
            rt.exec("cmd.exe /c start \"JEditor\"  /D "+filepath.substring(0,filepath.lastIndexOf("\\"))+" "+filepath.substring(filepath.lastIndexOf("\\")+1,filepath.length()-4)+"bat");
            status.setText(status.getText()+"\nlaunched successfully..");
        }catch(Exception e){
        }     
    }
    @FXML
    private void contentChanged(KeyEvent event) {
        if (!contentchanged) {
            contentchanged=true;
        }
        countLinesWords(codearea.getText());
    }
    void countLinesWords(String text)
    {
        int linecount=0;
        int wordcount=0;
        String li[]=text.split("\n");
        linecount=li.length;
        for(String str : li)
        {
            String words[]=str.split(" ");
            wordcount+=words.length;
        }
        countwords.setText("Lines  :\t"+linecount+"     Words:\t"+wordcount+"          Cursor Position :"+(codearea.getCaretPosition()+1)+"ch");
        
    }
    @FXML
    private void saveClicked(ActionEvent event) {
        if (contentchanged) {
            if (filepath==null){
                FileChooser filec=new FileChooser();
                filec.setTitle("Save File");
                File file=filec.showSaveDialog(null);
                if (file!=null) {
                    filepath=file.getPath();
                    saveFile(file);
                }
            }else{
                File f=new File(filepath);
                saveFile(f);
            }
        }
    }
    void saveFile(File file)
    {
        if (!filepath.contains(".java")) {
            filepath+=".java";
        }
        try{
                    BufferedWriter out;
                    out = new BufferedWriter(new FileWriter(filepath));
                    out.write(codearea.getText());
                    contentchanged=false;
                    out.close();
                     status.setText("saved in "+filepath);
            }catch(Exception e){ 
                            status.setText("file save error in "+filepath);
                    }    
    }
    @FXML
    private void saveAsClicked(ActionEvent event) {
                FileChooser filec=new FileChooser();
                filec.setTitle("SaveAs File");
//                filec.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(".java",".java"));
                File file=filec.showSaveDialog(null);
                if (file!=null) {
                    filepath=file.getPath();
                    saveFile(file);
                }else{ 
                    status.setText("saveAs Canceled by user");
                }
    }

    @FXML
    private void cutClicked(ActionEvent event) {
      caretpos=codearea.getCaretPosition();
      clipboard=codearea.getSelectedText();
      content=codearea.getText();
      content = content.replaceFirst(clipboard,"");
      codearea.setText(content);
        status.setText("Content removed and saved in local clipboard");
    }

    @FXML
    private void copyClicked(ActionEvent event) {
        clipboard=codearea.getSelectedText();
        status.setText("Copied to local clipboard");
    }

    @FXML
    private void pasteClicked(ActionEvent event) {
        caretpos=codearea.getCaretPosition();
        content=codearea.getText();
        StringBuilder bb=new StringBuilder(content);
        bb.insert(caretpos, clipboard);
        content=bb.toString();
        codearea.setText(content);
        codearea.positionCaret(caretpos+clipboard.length());
        status.setText("Content pasted from local clipboard");
    }
    @FXML
    private void dispSttsClikced(ActionEvent event) {
        String stts="File_Psth is :\t"+filepath;
        stts+="\nContent_Changed state is :\t"+contentchanged;
        stts+="\nclip board contains :\t"+clipboard;
        status.setText(stts);
    }
    @FXML
    private void keyPressedinCodeArea(KeyEvent event) {
        countLinesWords(codearea.getText());
    }
}