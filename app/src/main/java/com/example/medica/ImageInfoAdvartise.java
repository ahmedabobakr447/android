package com.example.medica;

public class ImageInfoAdvartise {

 String TitleText;
 String locationDescription;
 String imageUrl;
 String locationUrl;
 double lat;
 double longt;
 String CompleteAdsDate;
 String Email_Or_Phone_Id;

 public ImageInfoAdvartise() {
 }

 public ImageInfoAdvartise(String titleText, String locationDescription, String imageUrl, String locationUrl, double lat, double longt, String completeAdsDate, String email_Or_Phone_Id) {
  this.TitleText = titleText;
  this.locationDescription = locationDescription;
  this.imageUrl = imageUrl;
  this.locationUrl = locationUrl;
  this.lat = lat;
  this.longt = longt;
  this.CompleteAdsDate = completeAdsDate;
  this.Email_Or_Phone_Id = email_Or_Phone_Id;
 }

 public String getTitleText() {
  return TitleText;
 }

 public void setTitleText(String titleText) {
  this.TitleText = titleText;
 }

 public String getLocationDescription() {
  return locationDescription;
 }

 public void setLocationDescription(String locationDescription) {
  this.locationDescription = locationDescription;
 }

 public String getImageUrl() {
  return imageUrl;
 }

 public void setImageUrl(String imageUrl) {
  this.imageUrl = imageUrl;
 }

 public String getLocationUrl() {
  return locationUrl;
 }

 public void setLocationUrl(String locationUrl) {
  this.locationUrl = locationUrl;
 }

 public double getLat() {
  return lat;
 }

 public void setLat(double lat) {
  this.lat = lat;
 }

 public double getLongt() {
  return longt;
 }

 public void setLongt(double longt) {
  this.longt = longt;
 }

 public String getCompleteAdsDate() {
  return CompleteAdsDate;
 }

 public void setCompleteAdsDate(String completeAdsDate) {
  this.CompleteAdsDate = completeAdsDate;
 }

 public String getEmail_Or_Phone_Id() {
  return Email_Or_Phone_Id;
 }

 public void setEmail_Or_Phone_Id(String email_Or_Phone_Id) {
  this.Email_Or_Phone_Id = email_Or_Phone_Id;
 }
}
