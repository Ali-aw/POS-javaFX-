package controller;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class VoucherPrintable implements Printable {
    private String voucherNumber;
    private String companyName;
    private String address;
    private String date;
    private String item;
    private int quantity;
    private double price;
    private double total;

    public VoucherPrintable(String voucherNumber, String companyName, String address,
            String date, String item, int quantity, double price, double total) {
        this.voucherNumber = voucherNumber;
        this.companyName = companyName;
        this.address = address;
        this.date = date;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        g.drawString("Voucher Number: " + voucherNumber, 75, 75);
        g.drawString("Company Name: " + companyName, 75, 100);
        g.drawString("Address: " + address, 75, 125);
        g.drawString("Date: " + date, 75, 150);
        g.drawString("Item: " + item, 75, 175);
        g.drawString("Quantity: " + quantity, 75, 200);
        g.drawString("Price: " + price, 75, 225);
        g.drawString("Total: " + total, 75, 250);

        return PAGE_EXISTS;
    }
}