import UIKit
import SharedCode


class ViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource, ApplicationContractView {
    
    @IBOutlet private var label: UILabel!
    @IBOutlet weak var departurePicker: UIPickerView!
    @IBOutlet weak var arrivalPicker: UIPickerView!
    
    private let presenter: ApplicationContractPresenter = ApplicationPresenter()
    var stationData: [String] = [String]()
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
        let departureStation = stationData[departurePicker.selectedRow(inComponent: 0)]
        let arrivalStation = stationData[arrivalPicker.selectedRow(inComponent: 0)]
        let urlString: String = "https://www.lner.co.uk/travel-information/travelling-now/live-train-times/depart/\(departureStation)/\(arrivalStation)/#LiveDepResults"
        let urlComponents = URLComponents (string: urlString)!
        UIApplication.shared.open (urlComponents.url!)
        
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
    
}
    







