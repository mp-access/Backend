package ch.uzh.ifi.access.course;

import ch.uzh.ifi.access.course.Model.Assignment;
import ch.uzh.ifi.access.course.Model.Course;
import ch.uzh.ifi.access.course.Model.Exercise;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class RepoCacher {
 	static final String REPO_DIR = "repo";
	static final String REPO_URL = "https://github.com/mp-access/course_structure.git";

	// FOLDERS
	static final String ASSIGNMENT_FOLDER_PREFIX = "assignment";
	static final String EXERCISE_FOLDER_PREFIX = "exercise";
	static final String PUBLIC_FOLDER_NAME = "public";
	static final String PRIVATE_FOLDER_NAME = "private";
	static final String RESOURCE_FOLDER_NAME = "resource";
	static final String SOLUTION_FOLDER_NAME = "solution";

	// FILES
	static final String COURSE_FILE_NAME = "config.json";
	static final String ASSIGNMENT_FILE_NAME = "config.json";
	static final String EXERCISE_FILE_NAME = "config.json";
	static final String QUESTION_FILE_NAME = "description.md";

	static ObjectMapper mapper;

	List<String> media_ext = Arrays.asList(".jpg", ".jpeg", ".png", ".mp3", ".mp4");
	List<String> ignore_dir = Arrays.asList(".git");
	List<String> ignore_file = Arrays.asList(".gitattributes", ".gitignore", "README.md");

    public static Course retrieveCourseData() throws Exception
    {
        deleteDir(new File(REPO_DIR));

       	gitPull();

        RepoCacher cacher = new RepoCacher();

		mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		Course course = new Course(); //mapper.readValue(new File(REPO_DIR + "/config.json"), Course.class);

        cacher.cacheRepo(new File(REPO_DIR), course);

        return course;
    }

	static String readFile(File file){
		try{
			byte[] data = null;
			FileInputStream fis = new FileInputStream(file);
			data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			return new String(data, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();	
			return "";
		}
	}

    void cacheRepo(File file, Object context){
		if (file.isDirectory()) 
	  	{
	  		if(ignore_dir.contains(file.getName())) return;

	  		Object next_context = context;
			if(file.getName().startsWith(ASSIGNMENT_FOLDER_PREFIX)){
				Assignment assignment = new Assignment();
				((Course)context).assignments.add(assignment);
				next_context = assignment;
			}else if(file.getName().startsWith(EXERCISE_FOLDER_PREFIX)){
				Exercise exercise = new Exercise();
				((Assignment)context).exercises.add(exercise);
				next_context = exercise;
			}else if(file.getName().startsWith(PUBLIC_FOLDER_NAME)){
				listFiles(file, ((Exercise)context).public_files);
			}else if(file.getName().startsWith(PRIVATE_FOLDER_NAME)){
				listFiles(file, ((Exercise)context).private_files);
			}else if(file.getName().startsWith(RESOURCE_FOLDER_NAME)){
				listFiles(file, ((Exercise)context).resource_files);
			}else if(file.getName().startsWith(SOLUTION_FOLDER_NAME)){
				listFiles(file, ((Exercise)context).solution_files);
			}

	    	String[] children = file.list(); 
	    	for (int i=0; i < children.length; i++)
	      		cacheRepo(new File(file, children[i]), next_context);
	  	}else{
			if(ignore_file.contains(file.getName())) return;

			if(context instanceof Course) {
				if(file.getName().equals(COURSE_FILE_NAME)){
					try {
						((Course)context).set(mapper.readValue(file, Course.class));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}else if(context instanceof Assignment) {
				if(file.getName().equals(ASSIGNMENT_FILE_NAME)){
					try {
						((Assignment)context).set(mapper.readValue(file, Assignment.class));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}else if(context instanceof Exercise){
				if(file.getName().equals(QUESTION_FILE_NAME)){
					((Exercise)context).question =	readFile(file);
				}else if(file.getName().equals(EXERCISE_FILE_NAME)){
					try {
						((Exercise)context).set(mapper.readValue(file, Exercise.class));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
	  	}
    }

    public static void listFiles(File dir, List<File> fileList){
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i=0; i<children.length; i++)
				listFiles(new File(dir, children[i]), fileList);
		}else {
			fileList.add(dir);
		}
	}

	private static boolean deleteDir(File dir)
	{ 
	  if (dir.isDirectory()) 
	  { 
	    String[] children = dir.list(); 
	    for (int i=0; i<children.length; i++)
	        deleteDir(new File(dir, children[i]));
	  }
	  return dir.delete(); 
	} 

    static void gitPull() throws Exception{
        Git.cloneRepository()
                .setURI(REPO_URL)
                .setDirectory(new File(REPO_DIR))
                .call();
	}
 }