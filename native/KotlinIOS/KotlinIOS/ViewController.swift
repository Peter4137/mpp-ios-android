import UIKit
import SharedCode


class ViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource, ApplicationContractView, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet private var label: UILabel!
    @IBOutlet weak var departurePicker: UIPickerView!
    @IBOutlet weak var arrivalPicker: UIPickerView!
    
    private let presenter: ApplicationContractPresenter = ApplicationPresenter()
    private var stationData: [String] = [String]()
    private var departuresData: [String] = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.onViewTaken(view: self)
        
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return stationData.count
    }
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return stationData[row]
    }
    
    
    @IBAction func departureButton(_ sender: Any) {
        let departureStation: String = stationData[departurePicker.selectedRow(inComponent: 0)]
        let arrivalStation: String = stationData[arrivalPicker.selectedRow(inComponent: 0)]
        presenter.onButtonTapped(departureStation: departureStation, arrivalStation: arrivalStation, view: self)
    }
    
    func setDepartureDropdown(stationList: Array<String>) {
        self.departurePicker.delegate = self
        self.departurePicker.dataSource = self
        stationData = stationList
    }
    func setLabel(text: String) {
        label.text = text
    }
    func setArrivalDropdown(stationList: Array<String>) {
        self.arrivalPicker.delegate = self
        self.arrivalPicker.dataSource = self
        stationData = stationList
    }
    
    
    
    @IBOutlet weak var tableView: UITableView!
    func setupTableView() {
        tableView.delegate = self
        tableView.dataSource = self
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
    }
    func populateDeparturesTable(departuresList: Array<String>){
        departuresData = departuresList
        setupTableView()
    }
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return departuresData.count
    }
    func tableView(_ tableView: UITableView,
                 cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
        cell.textLabel!.text = departuresData[indexPath.row]
        return cell
    }
    
}
