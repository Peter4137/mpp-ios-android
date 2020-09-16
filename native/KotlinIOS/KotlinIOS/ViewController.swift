import UIKit
import SharedCode


class ViewController: UIViewController, ApplicationContractView {

    @IBOutlet weak var departurePicker: UIPickerView!
    @IBOutlet weak var arrivalPicker: UIPickerView!
    @IBOutlet private var label: UILabel!
    @IBOutlet weak var tableView: UITableView!

    private let presenter: ApplicationContractPresenter = ApplicationPresenter()
    private var stationData: [String] = []
    private var departuresData: [DepartureInformation] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.onViewTaken(view: self)
        self.departurePicker.delegate = self
        self.departurePicker.dataSource = self
        self.arrivalPicker.delegate = self
        self.arrivalPicker.dataSource = self
        setupTableView()
    }
    
    
    @IBAction func departureButton(_ sender: Any) {
        let departureStation: String = stationData[departurePicker.selectedRow(inComponent: 0)]
        let arrivalStation: String = stationData[arrivalPicker.selectedRow(inComponent: 0)]
        presenter.setDepartureStation(departureStation: departureStation)
        presenter.setArrivalStation(arrivalStation: arrivalStation)
        presenter.onButtonTapped()
    }
    
    func setLabel(text: String) {
        label.text = text
    }
    
}

extension ViewController: UIPickerViewDelegate, UIPickerViewDataSource {
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return stationData.count
    }
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return stationData[row]
    }
    func setDepartureDropdown(stationList: Array<String>) {
        stationData = stationList
        self.departurePicker.reloadComponent(0)
    }
    func setArrivalDropdown(stationList: Array<String>) {
        stationData = stationList
        self.arrivalPicker.reloadComponent(0)
    }
}

extension ViewController: UITableViewDelegate, UITableViewDataSource {
    
    func setupTableView() {
        tableView.delegate = self
        tableView.dataSource = self
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
        self.tableView.reloadData()
    }

    func populateDeparturesTable(departuresList: [DepartureInformation]) {
        departuresData = departuresList
        self.tableView.reloadData()
    }
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return departuresData.count
    }
    func tableView(_ tableView: UITableView,
                 cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "departureCell", for: indexPath) as! DeparturesTableViewCell
        let departure = departuresData[indexPath.row]
        cell.departureLabel!.text = departure.departureTime
        cell.arrivalLabel!.text = departure.arrivalTime
        cell.durationLabel!.text = departure.journeyTime
        cell.priceLabel!.text = departure.price
        cell.operatorLabel!.text = departure.trainOperator
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 80.0;//Choose your custom row height
    }
}
