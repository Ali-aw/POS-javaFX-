package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import common.Common;
import database.DBInitialize;
import functs.EditingCell;
//import functs.ReportGenerator;
import functs.SearchBarcode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ResourceBundle;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.ProductItem;

import model.Sale;
//import net.sf.jasperreports.engine.JRException;

public class MainCashierController {

	@FXML
	private Label lb_cashier_name;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private JFXButton bt_logout;

	@FXML
	private JFXTextField tf_barcode_search;

	@FXML
	private JFXTextField tf_exchage_rate;

	@FXML
	private JFXButton bt_new;

	@FXML
	private JFXButton btSave;

	@FXML
	private TableView<ProductItem> tb_total_item;

	private TableColumn<ProductItem, String> col_item_name;

	private TableColumn<ProductItem, String> col_item_category;

	private TableColumn<ProductItem, String> col_item_price;

	private TableColumn<ProductItem, String> col_item_barcode;

	private TableColumn<ProductItem, String> col_item_stock;

	private ObservableList<ProductItem> data = FXCollections.observableArrayList();
	private ObservableList<Sale> stockArray = FXCollections.observableArrayList();

	private static ObservableList<Sale> purchasedata = FXCollections.observableArrayList();;

	@FXML
	private TableView<Sale> tb_sale;

	private TableColumn<Sale, String> col_purchase_barcode;

	private TableColumn<Sale, String> col_purchase_name;

	private TableColumn<Sale, String> col_purchase_price;

	private TableColumn<Sale, Integer> col_purchase_quantity;

	private TableColumn<Sale, String> col_purchase_discount;

	private TableColumn<Sale, String> col_purchase_totalamount;

	@FXML
	private JFXButton bt_pay;

	@FXML
	private JFXTextField tf_total;

	@FXML
	private JFXTextField tf_pay_amount;

	@FXML
	private JFXTextField tf_change;

	@FXML
	private JFXTextField tf_name_search;

	@FXML
	private JFXButton btPrint;

	@FXML
	private Label lb_slip_no;

	@FXML
	private JFXButton bt_redeem;

	private Socket s;
	DataInputStream inputFromClient;
	DataOutputStream outputToClient;
	ServerSocket ss;
	int diff;

	private Thread th;
	/*
	 * private Thread th1; private Thread th2;
	 */

	// public static Thread thcashier;

	@FXML
	void onLogoutClick(ActionEvent event) {

		// scene transaction
		try {
			new LoginPg().start((Stage) bt_logout.getScene().getWindow());
			th.interrupt();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@FXML
	void initialize() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

		// col_item_id.setCellValueFactory(new PropertyValueFactory<ProductItemP,
		// String>("id"));
		btPrint.setVisible(false);

		assert tf_exchage_rate != null
				: "fx:id=\"tf_exchage_rate\" was not injected: check your FXML file 'cashier_main.fxml'.";

		assert lb_cashier_name != null
				: "fx:id=\"lb_cashier_name\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert bt_logout != null : "fx:id=\"bt_logout\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert tf_barcode_search != null
				: "fx:id=\"tf_barcode_search\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert tf_name_search != null
				: "fx:id=\"tf_name_search\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert bt_new != null : "fx:id=\"bt_barcode_scan\" was not injected: check your FXML file 'cashier_main.fxml'.";

		assert bt_redeem != null : "fx:id=\"bt_redeem\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert btSave != null
				: "fx:id=\"btSave\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert tb_total_item != null
				: "fx:id=\"tb_total_item\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert tb_sale != null : "fx:id=\"tb_sale\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert bt_pay != null : "fx:id=\"bt_pay\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert tf_total != null : "fx:id=\"tf_total\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert tf_pay_amount != null
				: "fx:id=\"tf_pay_amount\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert tf_change != null : "fx:id=\"tf_change\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert btPrint != null : "fx:id=\"btPrint\" was not injected: check your FXML file 'cashier_main.fxml'.";
		assert lb_slip_no != null : "fx:id=\"lb_slip_no\" was not injected: check your FXML file 'cashier_main.fxml'.";

		// set slip number
		new DBInitialize().DBInitialize();
		String previousgetpurchaseid = " SELECT `id` FROM `purchase` ORDER BY `id` DESC LIMIT 1 ";
		new DBInitialize();
		ResultSet rsslip = DBInitialize.statement.executeQuery(previousgetpurchaseid);
		String previousid = "";
		while (rsslip.next()) {
			previousid = rsslip.getString("id");
		}
		int nowid = Integer.parseInt(previousid) + 1;

		lb_slip_no.setText("" + nowid);
		Common.slipno = "" + nowid;

		th = new Thread(() -> {
			try {
				// tb_total_item.refresh();
				tf_name_search.clear();
				tf_barcode_search.clear();

				ss = new ServerSocket(3306);
				System.out.println("Server is running at port : 5000");

				while (true) {
					s = ss.accept();
					inputFromClient = new DataInputStream(s.getInputStream());
					outputToClient = new DataOutputStream(s.getOutputStream());

					String datafromandriod = inputFromClient.readUTF();
					// A8:81:95:8B:1C:AC

					System.out.println("Received from android: " + datafromandriod);
					// inputFromClient.close();
					// s.close();

					Platform.runLater(() -> tf_barcode_search.setText("" + datafromandriod));
					// tb_total_item.refresh();
					// tb_total_item.getItems().clear();
					// tb_total_item.refresh();
					data.clear();
					data = SearchBarcode.SearchByBarcode(datafromandriod);
					System.out.println("data from function db qr search is : " + data.get(0).getName());
					// tb_total_item.refresh();

					tb_total_item.setItems(data);
					tb_total_item.refresh();
					// tb_total_item.refresh();

				} // end of if
			} catch (Exception ex) {
			}
		});
		th.start();

		tf_total.setAlignment(Pos.BOTTOM_RIGHT);
		tf_pay_amount.setAlignment(Pos.BOTTOM_RIGHT);
		tf_change.setAlignment(Pos.BOTTOM_RIGHT);

		col_item_name = new TableColumn<ProductItem, String>("Name");
		col_item_category = new TableColumn<ProductItem, String>("Category");
		col_item_price = new TableColumn<ProductItem, String>("Price");
		col_item_barcode = new TableColumn<ProductItem, String>("Barcode");
		col_item_stock = new TableColumn<ProductItem, String>("Stock");

		col_item_name.setMinWidth(200.0);
		col_item_category.setMinWidth(160.0);
		col_item_price.setMinWidth(100.0);
		col_item_barcode.setMinWidth(220.0);
		col_item_stock.setMinWidth(90.0);

		col_item_name.setStyle("-fx-font-size: 18");
		col_item_category.setStyle("-fx-font-size: 18");
		col_item_price.setStyle("-fx-font-size: 18");
		col_item_barcode.setStyle("-fx-font-size: 18");
		col_item_stock.setStyle("-fx-font-size: 18");

		col_item_name.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("name"));
		col_item_category.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("categoryname"));
		col_item_price.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("price"));
		col_item_barcode.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("barcode"));
		col_item_stock.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("stockamount"));

		tb_total_item.getColumns().addAll(col_item_barcode, col_item_name, col_item_category, col_item_price,
				col_item_stock);

		// purchase table
		col_purchase_barcode = new TableColumn<Sale, String>("Barcode");
		col_purchase_name = new TableColumn<Sale, String>("Name");
		col_purchase_price = new TableColumn<Sale, String>("Price");
		col_purchase_quantity = new TableColumn<Sale, Integer>("Quantity");
		col_purchase_discount = new TableColumn<Sale, String>("Discount");
		col_purchase_totalamount = new TableColumn<Sale, String>("TotalAmount");

		col_purchase_barcode.setMinWidth(120.0);
		col_purchase_name.setMinWidth(90.0);
		col_purchase_price.setMinWidth(60.0);
		col_purchase_quantity.setMinWidth(25.0);
		col_purchase_discount.setMinWidth(25.0);
		col_purchase_totalamount.setMinWidth(120.0);

		col_purchase_barcode.setStyle("-fx-font-size: 15");
		col_purchase_name.setStyle("-fx-font-size: 15");
		col_purchase_price.setStyle("-fx-font-size: 15");
		col_purchase_quantity.setStyle("-fx-font-size: 15");
		col_purchase_discount.setStyle("-fx-font-size: 15");
		col_purchase_totalamount.setStyle("-fx-font-size: 15");

		Callback<TableColumn<Sale, Integer>, TableCell<Sale, Integer>> cellFactory = (
				TableColumn<Sale, Integer> param) -> new EditingCell();

		col_purchase_barcode.setCellValueFactory(new PropertyValueFactory<Sale, String>("barcode"));
		col_purchase_name.setCellValueFactory(new PropertyValueFactory<Sale, String>("name"));
		col_purchase_price.setCellValueFactory(new PropertyValueFactory<Sale, String>("unitamount"));

		col_purchase_quantity.setCellValueFactory(new PropertyValueFactory<Sale, Integer>("quantity"));
		col_purchase_quantity.setCellFactory(cellFactory);
		col_purchase_quantity.setOnEditCommit(new EventHandler<CellEditEvent<Sale, Integer>>() {
			@Override
			public void handle(CellEditEvent<Sale, Integer> t) {
				((Sale) t.getTableView().getItems().get(t.getTablePosition().getRow())).setQuantity((t.getNewValue()));

				System.out.println("Qty edit Working");

				t.getRowValue().setQuantity(t.getNewValue());
				double qty = ((Sale) t.getTableView().getItems().get(t.getTablePosition().getRow())).getQuantity();

				// double unitprice = t.getRowValue().getUnitamount();
				// double total1 = unitprice * qty;

				double total = 0;
				// promotion compute

				System.out.println("buy get is " + Common.buygetdata);

				// t.getRowValue().Total amount is :(t.getTotalamount());
				tb_sale.refresh();
				tb_sale.getColumns().get(0).setVisible(false);
				tb_sale.getColumns().get(0).setVisible(true);

				double finaltotal = 0;
				// Sale i;

				ResultSet rss3;
				int rate3 = 0;
				try {
					rss3 = DBInitialize.statement.executeQuery("select rate from `ratetable`");
					while (rss3.next()) {
						rate3 = Integer.parseInt(rss3.getString(1));

					}
				} catch (SQLException e1) {

					e1.printStackTrace();
				}
				for (Sale i : tb_sale.getItems()) {
					// + i.getRowValue().getUnitamount()
					finaltotal = finaltotal + i.getUnitamount() * rate3 * i.getQuantity();
					System.out.println(
							"ua: " + "rate: " + rate3 + "qtty" + i.getQuantity());
				}
				tf_total.setText("" + finaltotal);
				// System.out.println(
				// "final total:" + finaltotal + "totalall: " + totalall + "rate3: " + rate3 +
				// "qty:"+qty);

				Common.totalAmount = Double.parseDouble(tf_total.getText());
				// System.out.println("Total amount is : " + Common.totalAmount);
				// tb_sale.refresh();
			}
		});

		col_purchase_discount.setCellValueFactory(new PropertyValueFactory<Sale, String>("discount"));

		/*
		 * col_purchase_totalamount.setCellValueFactory(new
		 * Callback<CellDataFeatures<Sale, String>, ObservableValue<String>>() {
		 * 
		 * 
		 * 
		 * public ObservableValue<String> call(CellDataFeatures<Sale, String> param) {
		 * 
		 * 
		 * double total = param.getValue().getQuantity() *
		 * Double.parseDouble(param.getValue().getUnitamount());
		 * 
		 * 
		 * return new SimpleStringProperty(""+total);
		 * 
		 * }
		 * 
		 * });
		 */
		col_purchase_totalamount.setCellValueFactory(new PropertyValueFactory<Sale, String>("totalamount"));

		/*
		 * col_purchase_totalamount.setCellValueFactory(cellData -> { Sale data =
		 * cellData.getValue(); return Bindings.createDoubleBinding( () -> { try {
		 * double price = data.getUnitamount(); double quantity = data.getQuantity();
		 * return price * quantity ; } catch (NumberFormatException nfe) { return 0 ; }
		 * }, data.totalamountProperty(), data.quantityProperty() ); });
		 */
		tb_sale.setEditable(true);
		tb_sale.setItems(purchasedata);
		tb_sale.getColumns().addAll(col_purchase_barcode, col_purchase_name, col_purchase_price, col_purchase_quantity,
				col_purchase_discount, col_purchase_totalamount);
		tb_sale.refresh();

		/*
		 * col_item_id.setCellValueFactory(new Callback<CellDataFeatures<ProductItem,
		 * String>, ObservableValue<String>>() {
		 * 
		 * public ObservableValue<String> call(CellDataFeatures<ProductItem, String>
		 * param) {
		 * 
		 * return new SimpleStringProperty(""); } });
		 */

		// set cashier name
		lb_cashier_name.setText(Common.cashierrec.getName());

		// get data from db and set it to table
		new DBInitialize().DBInitialize();

		String tablequery = "SELECT productitems.barcode, productitems.name, productcategory.name, productitems.price, supplier.companyname, productitems.dateadded, productitems.stockamount, productitems.expireddate FROM productitems, supplier,productcategory WHERE productitems.categoryid = productcategory.id AND productitems.supplierid = supplier.id ORDER BY productitems.barcode DESC;";
		ResultSet rs = DBInitialize.statement.executeQuery(tablequery);
		while (rs.next()) {

			ProductItem p = new ProductItem();
			p.setBarcode(rs.getString(1));
			p.setName(rs.getString(2));
			p.setCategoryname(rs.getString(3));
			p.setPrice(rs.getString(4));
			p.setSuppliername(rs.getString(5));
			p.setDateadded(rs.getString(6));
			p.setStockamount(rs.getString(7));
			p.setExpiredate(rs.getString(8));

			data.add(p);
		}

		tb_total_item.setItems(data);
		// hon lama a3ml double click 3a row bl table l total
		tb_total_item.setRowFactory(t -> {
			TableRow<ProductItem> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				// get data from selected row
				// ProductItem productItem =
				// tb_total_item.getSelectionModel().getSelectedItem();
				// System.out.println("Select row is : "+productItem.getName());

				if (e.getClickCount() == 2 && (!row.isEmpty())) {
					String dispercentage = "0";
					String dismore = "Buy 0 Get 0";

					ProductItem product = tb_total_item.getSelectionModel().getSelectedItem();
					System.out.println("Double click is: " + product.getName());

					// get discount form db
					String discountQuery = "SELECT promotion.percentage, promotion.description FROM `promotion` WHERE promotion.productid = '"
							+ product.getBarcode() + "';";
					try {
						new DBInitialize().DBInitialize();
						new DBInitialize();
						ResultSet rsd = DBInitialize.statement.executeQuery(discountQuery);

						if (rsd.next()) {
							dispercentage = rsd.getString(1);
							dismore = rsd.getString(2);
						} else {
							System.out.println("no discount");
						}

						System.out
								.println("percentage from db is ::::" + dispercentage + " &&&& more is ::::" + dismore);
						product.setDiscount(dispercentage);
						product.setDiscountmore(dismore);

					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| SQLException e1) {

						e1.printStackTrace();
					}

					// create virtual sale item
					Sale sa = new Sale();

					double total = 0;
					// promotion compute
					if (dispercentage.equals("0")) {

						sa.setTotalamount(Double.parseDouble(product.getPrice()));
						System.out.println("dispercent 0 is working");
					} else {

						System.out.println("unit price is :::" + product.getPrice());
						System.out.println("discount percnet is :::" + dispercentage);
						// double tominuspromotion = Double.parseDouble(product.getPrice())
						// * (Double.parseDouble(dispercentage) / 100);
						ResultSet rss4;
						int rate4 = 0;
						try {
							rss4 = DBInitialize.statement.executeQuery("select rate from `ratetable`");
							while (rss4.next()) {
								rate4 = Integer.parseInt(rss4.getString(1));

							}
						} catch (SQLException e1) {

							e1.printStackTrace();
						}
						for (Sale i : tb_sale.getItems()) {
							total = total + Double.parseDouble(product.getPrice()) * rate4 * i.getQuantity();
						}

						System.out.println("unit amount after discount is : " + total);

						sa.setTotalamount(Double.parseDouble(total + ""));
						System.out.println("dispercent compute is working");
					}

					if (dismore.equals("Buy 0 Get 0")) {
						// do nothing
					} else {
						Common.buygetdata.add(product.getBarcode());
					}

					System.out.println("buy get is :::::::::::::" + Common.buygetdata);

					// set sale data
					sa.setBarcode("" + product.getBarcode());
					sa.setName(product.getName());
					sa.setQuantity(1);
					sa.setUnitamount(Double.parseDouble(product.getPrice()));
					sa.setDiscount(Double.parseDouble(product.getDiscount()));
					sa.setDiscountmore(product.getDiscountmore());

					// double totalAmount = count * Double.parseDouble(sale.getUnitamount());
					// sa.setTotalamount(Double.parseDouble(""+product.getPrice()));

					purchasedata.add(sa);
					stockArray.add(sa);
					tb_sale.refresh();
					int totalall = 0;
					ResultSet rss1;
					double finaltotal = 0;

					System.out.println("final:::" + finaltotal);

					int rate = 0;
					try {
						rss1 = DBInitialize.statement.executeQuery("select rate from ratetable");
						while (rss1.next()) {
							rate = Integer.parseInt(rss1.getString(1));

						}
					} catch (SQLException e1) {

						e1.printStackTrace();
					}
					// for (Sale i : tb_sale.getItems()) {
					// // + i.getRowValue().getUnitamount()
					// finaltotal = finaltotal + i.getUnitamount() * rate * i.getQuantity();

					// }

					// final int finaltotal = totalall * rate;
					for (Sale i : tb_sale.getItems()) {
						finaltotal = finaltotal + i.getUnitamount() * rate * i.getQuantity();
					}
					System.out.println("rateee::" + rate);
					tf_total.setText("" + finaltotal);
					Common.totalAmount = Double.parseDouble(tf_total.getText());
					// System.out.println("Total amount is : " + Common.totalAmount);

					// for (Sale s : stockArray) {

					// String query44 = "select productitems.stockamount from `productitems` where
					// barcode="
					// + s.getBarcode();
					// new DBInitialize();
					// try (ResultSet rsslip2 = DBInitialize.statement.executeQuery(query44)) {
					// try {
					// while (rsslip2.next()) {

					// diff = Integer.parseInt(rsslip2.getString(1)) -
					// s.getQuantity();
					// System.out.println("diffffff" + diff);
					// break;
					// }
					// } catch (NumberFormatException | SQLException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
					// } catch (SQLException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
					// String query4 = "UPDATE `productitems` set `stockamount`= " + diff + " where
					// `barcode`=\""
					// + s.getBarcode() + "\"";
					// System.out.println("queryyyyyyyyyyyyyyyyyyyy dbl clck: " + query4);

					// try {

					// DBInitialize.statement.executeUpdate(query4);
					// } catch (SQLException ex) {

					// ex.printStackTrace();
					// }

					// }
				}
			});

			return row;
		});

		tb_sale.setRowFactory(t -> {
			TableRow<Sale> r = new TableRow<>();
			r.setOnMouseClicked(e -> {
				// get data from selected row
				// ProductItem productItem =
				// tb_total_item.getSelectionModel().getSelectedItem();
				// System.out.println("Select row is : "+productItem.getName());

				if (e.getClickCount() == 2 && (!r.isEmpty())) {
					Sale sale = tb_sale.getSelectionModel().getSelectedItem();
					System.out.println("sale Double click is: " + sale.getName());

				}

				tb_sale.refresh();
			});

			final ContextMenu rowMenu = new ContextMenu();

			MenuItem removeItem = new MenuItem("Delete");
			removeItem.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					Sale s = tb_sale.getSelectionModel().getSelectedItem();

					Alert alert = new Alert(AlertType.CONFIRMATION, "Are U Sure To Delete " + s.getName() + " ?",
							ButtonType.YES, ButtonType.NO);
					alert.showAndWait();

					if (alert.getResult() == ButtonType.YES) {
						// do stuff

						// reduce all total ammount
						Sale se = purchasedata.get(tb_sale.getSelectionModel().getFocusedIndex());
						int rate2 = 0;
						ResultSet rss2;

						try {
							rss2 = DBInitialize.statement.executeQuery("select rate from ratetable");
							while (rss2.next()) {
								rate2 = Integer.parseInt(rss2.getString(1));

							}
						} catch (SQLException e1) {

							e1.printStackTrace();
						}
						System.out.println("s.getTotalamount(): " + s.getTotalamount() * rate2);
						Common.totalAmount = Common.totalAmount - (s.getTotalamount() * rate2);
						tf_total.setText("" + Common.totalAmount);
						tf_pay_amount.clear();
						// tf_change.clear();
						purchasedata.remove(tb_sale.getSelectionModel().getFocusedIndex());

						tb_sale.refresh();

					}
				}
			});
			rowMenu.getItems().addAll(removeItem);

			// only display context menu for non-null items:
			r.contextMenuProperty().bind(
					Bindings.when(Bindings.isNotNull(r.itemProperty())).then(rowMenu).otherwise((ContextMenu) null));

			return r;
		});

	}

	// for screen transaction from login to admin panel
	public class LoginPg extends Application {

		@Override
		public void start(Stage primaryStage) throws Exception {
			Parent root = FXMLLoader.load(getClass().getResource("/ui/Page_login.fxml"));

			Scene scene = new Scene(root, 1320, 700);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Cashier");
			primaryStage.setFullScreen(true);
			// primaryStage.sizeToScene();
			// primaryStage.setResizable(false);
			// primaryStage.setMaximized(false);
			primaryStage.show();
		}
	}

	Sale s2;

	// search product item table by barcode or name
	@FXML
	void tfTypeSearchAction(KeyEvent event) throws ClassNotFoundException, SQLException, InterruptedException {

		th.sleep(1);

		tf_name_search.clear();

		String searchKey = tf_barcode_search.getText().toString();
		System.out.println("key entered is : " + searchKey);

		String get_barcode_db = "SELECT * FROM `productitems` WHERE  productitems.barcode='" + searchKey + "';";
		ResultSet rs1 = DBInitialize.statement.executeQuery(get_barcode_db);
		System.out.println("::::" + rs1.toString());

		if (rs1 != null) {
			System.out.println("in!null");

			// ObservableList<Sale> row1 = FXCollections.observableArrayList();
			while (rs1.next()) {
				s2 = new Sale();

				s2.setBarcode(rs1.getString(6));
				s2.setName(rs1.getString(1));
				s2.setUnitamount(Double.parseDouble(rs1.getString(5)));// price
				s2.setQuantity(1);
				s2.setDiscount(0.0);
				s2.setTotalamount(Double.parseDouble(rs1.getString(5)));
				// row1.add(s2);
				tb_sale.getItems().add(s2);
				tf_barcode_search.setText("");
			}

			//
			// try {
			// Thread.sleep(2000);

			// } catch (Exception e) {
			// System.out.println("" + e);
			// }

			tb_sale.refresh();

			tf_barcode_search.requestFocus();
			// tb_sale.refresh();
			int totalall = 0;
			for (Sale i : tb_sale.getItems()) {
				totalall += i.getTotalamount();
			}
			int rate2 = 0;

			ResultSet rss2;

			try {
				rss2 = DBInitialize.statement.executeQuery("select rate from ratetable");
				while (rss2.next()) {
					rate2 = Integer.parseInt(rss2.getString(1));

				}
			} catch (SQLException e1) {

				e1.printStackTrace();
			}

			final int finaltotal = totalall * rate2;
			tf_total.setText("" + finaltotal);

			// tf_total.setText("" + totalall);
			Common.totalAmount = Double.parseDouble(tf_total.getText());
			System.out.println("Total amount is : " + Common.totalAmount + "s2barcode: " + s2.getBarcode());

			stockArray.add(s2);
			System.out.println("!!!!!: " + stockArray.get(0).getBarcode());

		} else {
			System.out.println("not in system");

			Alert aler1 = new Alert(AlertType.ERROR, "العنصر غير موجود!!");
			aler1.showAndWait();
		}

	}

	@FXML
	void tfExchangeRateAction1(KeyEvent event) throws InterruptedException {

		if (event.getCode() == KeyCode.ENTER) {
			System.out.println("in enter");
			Alert alertrate = new Alert(AlertType.CONFIRMATION, "هل انت متاكد ؟ ",
					ButtonType.YES, ButtonType.NO);
			alertrate.showAndWait();

			if (alertrate.getResult() == ButtonType.YES) {

				if (tf_exchage_rate.getText().equals("")) {
					Alert al2 = new Alert(AlertType.ERROR, "قم بادخال سعر الصيرفة!");
					al2.showAndWait();
					tf_exchage_rate.requestFocus();

					System.out.println("!");
				} else {

					String query2 = "update ratetable set ratetable.rate="
							+ Integer.parseInt(tf_exchage_rate.getText());
					System.out.println("v::" + tf_exchage_rate.getText());

					try {

						DBInitialize.statement.executeUpdate(query2);
					} catch (SQLException e) {

						e.printStackTrace();
					}

				}

			}
		}

	}

	@FXML
	void tfNameSearchAction(KeyEvent event) throws InterruptedException {

		th.sleep(1);
		tf_barcode_search.clear();

		String searchKey = tf_name_search.getText().toString();
		System.out.println("key entered is : " + searchKey);
		String query = "SELECT productitems.barcode, productitems.name, productcategory.name, productitems.price, supplier.companyname, productitems.dateadded, productitems.stockamount, productitems.expireddate FROM productitems, supplier,productcategory WHERE productitems.categoryid = productcategory.id AND productitems.supplierid = supplier.id AND productitems.name LIKE '"
				+ searchKey + "%'";

		// new DBInitialize().DBInitialize();

		System.out.println("working");
		try {
			// ResultSet rs = st.executeQuery("SELECT * FROM USER");
			ResultSet rs = DBInitialize.statement.executeQuery(query);
			ObservableList<ProductItem> row = FXCollections.observableArrayList();
			while (rs.next()) {
				ProductItem p = new ProductItem();
				p.setBarcode(rs.getString(1));
				p.setName(rs.getString(2));
				p.setCategoryname(rs.getString(3));
				p.setPrice(rs.getString(4));
				p.setSuppliername(rs.getString(5));
				p.setDateadded(rs.getString(6));
				p.setStockamount(rs.getString(7));
				p.setExpiredate(rs.getString(8));

				row.add(p);
			}
			tb_total_item.setItems(row);
			// System.out.println("working1"+data);

			// tb_total_item.getItems().clear();
			// tb_total_item.setItems(data);

			System.out.println("working2");
			// data.getItems().addAll(row);
		} catch (SQLException ex) {

		}

	}

	@FXML
	void onEnterButtonClick(ActionEvent event) {

		if (tf_pay_amount.getText().equals("")) {
			Alert al = new Alert(AlertType.ERROR, "No Input!");
			al.showAndWait();
		} else if (tf_pay_amount.getText().matches(".*[a-zA-Z]+.*")) {
			Alert al = new Alert(AlertType.ERROR, "Please input the right amount in number!");
			al.showAndWait();
		} else if (Double.parseDouble(tf_pay_amount.getText()) < Common.totalAmount) {
			// do nothing
			Alert al = new Alert(AlertType.ERROR, "Invalid amount!");
			al.showAndWait();
			tf_pay_amount.clear();
		} else {
			try {
				Common.payamount = Double.parseDouble(tf_pay_amount.getText());
				Common.change = Common.payamount - Common.totalAmount;
				tf_change.setText("" + Common.change);
				System.out.println("Total Amount is: " + Common.totalAmount);
				System.out.println("Pay Amount is: " + Common.payamount);
				System.out.println("Change is: " + Common.change);
			} catch (Exception ex) {
				System.out.println("Error in payamount: " + ex.getMessage());
			}

		} // end of else
	}

	// customer number textfield
	public class NumberTextField extends TextField {

		@Override
		public void replaceText(int start, int end, String text) {
			if (validate(text)) {
				super.replaceText(start, end, text);
			}
		}

		@Override
		public void replaceSelection(String text) {
			if (validate(text)) {
				super.replaceSelection(text);
			}
		}

		private boolean validate(String text) {
			return text.matches("[0-9]*");
		}
	}

	@FXML
	void onbtPrintClick(ActionEvent event)
			throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException,
			FileNotFoundException {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable((Printable) new VoucherPrintable("lb_slip_no.getText().toString()", "S-Tech", "Shaqra",
				"LocalDate.now(null).toString()",
				"LocalTime.now().toString()", 1, 2.2, 3.2));

		try {
			job.print();
		} catch (PrinterException e) {
			e.printStackTrace();
		}

	}

	@FXML
	void onbtSaveClick(ActionEvent event) throws SQLException {
		System.out.println("hii");
		Alert alertsave = new Alert(AlertType.CONFIRMATION, "هل انت متاكد ؟ ",
				ButtonType.YES, ButtonType.NO);
		alertsave.showAndWait();

		if (alertsave.getResult() == ButtonType.YES) {

			for (Sale s : stockArray) {

				String query44 = "select productitems.stockamount from `productitems` where barcode="
						+ s.getBarcode();
				new DBInitialize();
				ResultSet rsslip1 = DBInitialize.statement.executeQuery(query44);
				while (rsslip1.next()) {

					diff = Integer.parseInt(rsslip1.getString(1)) -
							s.getQuantity();
					System.out.println("diffffff" + diff);
					break;
				}
				String query4 = "UPDATE `productitems` set `stockamount`= " + diff + " where `barcode`=\""
						+ s.getBarcode() + "\"";
				System.out.println("queryyyyyyyyyyyyyyyyyyyy: " + query4);

				try {

					DBInitialize.statement.executeUpdate(query4);
				} catch (SQLException e) {

					e.printStackTrace();
				}

			}
		}

	}

	@FXML
	void onbtNewClick(ActionEvent event)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {

		Common.buygetdata.clear();
		Common.saleitemsdatafromsaletable.clear();
		// set slip number
		new DBInitialize().DBInitialize();
		String previousgetpurchaseid = " SELECT `id` FROM `purchase` ORDER BY `id` DESC LIMIT 1 ";
		new DBInitialize();
		ResultSet rsslip = DBInitialize.statement.executeQuery(previousgetpurchaseid);
		String previousid = "";
		while (rsslip.next()) {
			previousid = rsslip.getString("id");
		}
		int nowid = Integer.parseInt(previousid) + 1;

		lb_slip_no.setText("" + nowid);
		Common.slipno = "" + nowid;

		// clear sale data
		purchasedata.clear();
		tb_sale.refresh();
		Common.totalAmount = 0;
		tf_total.clear();
		tf_pay_amount.clear();
		tf_change.clear();

		// update instock table
		// get data from db and set it to table
		new DBInitialize().DBInitialize();
		data.clear();

		String tablequery = "SELECT productitems.barcode, productitems.name, productcategory.name, productitems.price, supplier.companyname, productitems.dateadded, productitems.stockamount, productitems.expireddate FROM productitems, supplier,productcategory WHERE productitems.categoryid = productcategory.id AND productitems.supplierid = supplier.id ORDER BY productitems.barcode DESC;";
		ResultSet rs = DBInitialize.statement.executeQuery(tablequery);
		while (rs.next()) {

			ProductItem p = new ProductItem();
			p.setBarcode(rs.getString(1));
			p.setName(rs.getString(2));
			p.setCategoryname(rs.getString(3));
			p.setPrice(rs.getString(4));
			p.setSuppliername(rs.getString(5));
			p.setDateadded(rs.getString(6));
			p.setStockamount(rs.getString(7));
			p.setExpiredate(rs.getString(8));

			data.addAll(p);
		}

		tb_total_item.refresh();

	}

}
