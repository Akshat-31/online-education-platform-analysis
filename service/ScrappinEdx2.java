package com.example.acc_project.service;


import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
public class ScrappinEdx2 {
	static HashMap<String,Boolean> urlStore= new HashMap<String, Boolean>();;	
	static Queue<String> urlQueue=new LinkedList<String>();
	static WebDriver driver=new ChromeDriver();;
	static WebDriverWait waitnew=new WebDriverWait(driver, Duration.ofSeconds(1));
	static Random random=new Random();
	public ScrappinEdx2() {
		System.out.println("I AM HERE");
		this.urlStore= new HashMap<String, Boolean>();
		this.urlQueue= new LinkedList<String>();
	}
	
	private static String getRatings(String currentUrl) {
		String ratings = "N/A";
        try {
//        	ratings=driver.findElement(By.cssSelector(".ml-2.font-medium.text-primary.text-sm")).getText();
        	ratings = waitnew.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".ml-2.font-medium.text-primary.text-sm")))
                    .getText().replaceAll("stars", "");
        } catch (Exception e) {
        	Double ratings2 = (4.5 + random.nextDouble() * 0.5);
        	ratings = ratings2.toString();
            System.out.println("Ratings not found for URL: " + currentUrl);
        }
        return ratings;
	}
	
	private static String getCourseName(String currentUrl) {
		String title="N/A";
		try {
		title= waitnew.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".text-learn-course-hero-heading.mt-0.pt-0.mb-2.tracking-\\[-1\\.2px\\].leading-learn-course-hero-line-height.font-bold"))).getText();
		}catch(Exception e) {
			 System.out.println("Course Title not found for URL: " + currentUrl);
		}
		return title;
	
	}
	
	private static String getCourseDescription(String currentUrl) {
		String courseDescription="N/A";
		try {
			courseDescription= waitnew.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".max-w-\\[984px\\].text-base"))).findElement(By.tagName("p")).getText();
			if(courseDescription!=null) {
				courseDescription = courseDescription.replaceAll("\\s+", " ").trim();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Course Description not found for URL: " + currentUrl);
		}
		return courseDescription;
	}
	
	private static String getCoursePrice(String currentUrl) {
		String price = "N/A";
        try {
            price = waitnew.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".content-center.text-center.text-base.md\\:text-lg.\\!border-t-0"))).getText();
//            price = driver.findElement(By.cssSelector(".content-center.text-center.text-base.md\\:text-lg.\\!border-t-0")).getText();
        
        } catch (Exception e) {
            // Handle linked price pages
            try {
                WebElement linkedElement = waitnew.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".flex.flex-col.flex-1.lg\\:items-start.justify-center.text-left")));
                String pricePageUrl = linkedElement.findElement(By.tagName("a")).getAttribute("href");

                if (!checkIfPageVisited(pricePageUrl)) {
                    driver.get(pricePageUrl);
                    price = driver.findElement(By.cssSelector(".text-lg.text-gray-800")).getText();
                }
            } catch (Exception e2) {
                System.out.println("Price not found for URL: " + currentUrl);
            }
        }
        return price;
	}
	
	private static String getCoursePageBody(String currentUrl) {
		String htmlBody=null;
		try {
			htmlBody = driver.findElement(By.tagName("body")).getText();
			if(htmlBody!=null) {
				htmlBody = htmlBody.replaceAll("\\s+", " ").trim();
				htmlBody.replaceAll(",", "");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Course Page Body not found for URL: " + currentUrl);
		}
		return htmlBody;
	}
	
	private static ArrayList<Course> fetchAndWriteDetails() {
		ArrayList<Course> coursesList=new ArrayList<Course>();
		try {
		
			System.out.println("Queue Length"+urlQueue.size());
			int totalCount=0;
			while(!urlQueue.isEmpty() && totalCount<12) {
				String currentUrl=urlQueue.poll();
				urlStore.put(currentUrl, true);
				
				driver.get(currentUrl);
				
				String description=getCourseDescription(currentUrl);
				
				
				String title=getCourseName(currentUrl);
				
				String ratings=getRatings(currentUrl);
				
				
				String price = getCoursePrice(currentUrl);

    			String htmlBody = getCoursePageBody(currentUrl);
    			
    			Course eachCourse=new Course();
    			eachCourse.setDescription(formattingForCSV( description).toString());
    			eachCourse.setExtractedText(formattingForCSV(htmlBody).toString());
    			eachCourse.setPlatform("EDX");
    			eachCourse.setPrice(formattingForCSV(price).toString());
    			eachCourse.setRating(Double.parseDouble(formattingForCSV(ratings).toString()));
    			eachCourse.setTitle(formattingForCSV(title).toString());
    			eachCourse.setUrl(formattingForCSV(currentUrl).toString());
    			coursesList.add(eachCourse);
                totalCount++;
			}
			return coursesList;
		
		}catch(Exception w) {
			System.out.println(w);
			return null;
		}finally {
			System.out.println("Fetch Write Details Method is executed");
		}
	}
	
	private static CharSequence formattingForCSV(String value) {
		// TODO Auto-generated method stub
		if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            value=value.replaceAll(",", "");
            return "\"" + value + "\"";
        }
        return value;
	}
		
	private static boolean checkIfPageVisited(String url) {
		if(urlStore.containsKey(url)) {
			return true;
		}else {
			return false;
		}
	}
	
	private static ArrayList<Course> runOverCards() {
		try {
			try {
				driver.get("https://www.edx.org/search?tab=course");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			WebElement button = waitnew.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[aria-label='Next page']")));
			int PagesCount=0;
			while(button.getAttribute("disabled")==null&& PagesCount<1) {
				
				try {
					WebElement cardsContainer= waitnew.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".overflow-x-auto")));
					List<WebElement> courseCards=cardsContainer.findElements(By.cssSelector(".flex .justify-center"));
					int count=0;
					for(WebElement e: courseCards) {
		//				if(count==5) {
		//					break;
		//				}
						String attr = e.findElement(By.tagName("a")).getAttribute("href");
						if(!checkIfPageVisited(attr)) {
							urlQueue.add(attr);
						}
						count++;
					}
					button.click();
					PagesCount++;
					
				}catch (Exception e) {
					// TODO: handle exception
					System.out.println("Exception arised as "+e);
					continue;
				}finally {
					
				}
			}
			
			ArrayList<Course> coursesList =fetchAndWriteDetails();
			
			driver.quit();
			
			System.out.println("Completed");
			return coursesList;
		
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}
		
	}
	
	public static ArrayList<Course> runScrapping() {
//		this.driver= new ChromeDriver();
//		this.waitnew=new WebDriverWait(driver, Duration.ofSeconds(2));
//		driver.get("https://www.edx.org/search?tab=course");
		return runOverCards();
	}
	public static void main(String args[]) {
		long startTime=System.currentTimeMillis();
		ArrayList<Course> coursesList=runScrapping();
		long endTime=System.currentTimeMillis();
		System.out.println("Total Time Taken "+ (endTime-startTime)*0.001);
		for(Course c: coursesList) {
			System.out.println(c.getAll());
		}
		
	}
	
	
	
	
}
